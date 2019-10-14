package ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.AnyThread
import androidx.annotation.CallSuper
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.adapters.ItemAdapter
import gromiloff.observer.impl.ObserverFragment
import pro.gromiloff.mos.counters.R
import java.util.*

abstract class BaseListFragment<VM : ViewModel, ModelData, ListItem : IItem<*, *>>
    : ObserverFragment<VM>(), SwipeRefreshLayout.OnRefreshListener {

    protected var list: RecyclerView? = null
    protected var adapter: FastAdapter<ListItem>? = null
    // private var footerAdapter: LoadNextPageAdapter? = null
    protected val headerAdapter = ItemAdapter<IItem<Any, *>>()
    protected val fastAdapter = ItemAdapter<ListItem>()
    private var swipeRefreshLayout: SwipeRefreshLayout? = null
    private var empty: View? = null

    // protected open fun loadNext(page: Int) {}

    open fun listId() = android.R.id.list

    /*override fun update(o: FastObserver, arg: Any?) {
        if (arg is UnknownNextPage) {
            loadNext(arg.page)
        } else super.update(o, arg)
    }*/

    override fun layoutId() = R.layout.wrapper_list

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.list = view.findViewById(listId())

        this.swipeRefreshLayout = view.findViewById(R.id.id_swipe_refresh_layout)
        this.swipeRefreshLayout?.isEnabled = swipeRefreshSupported()
        this.swipeRefreshLayout?.setOnRefreshListener(this)

        this.empty = view.findViewById(android.R.id.empty)
        this.empty?.visibility = View.GONE

        //   changeFootedState(false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        installAdapter(this.list!!, createAndGetAdapter())
        this.list?.layoutManager = getLayoutManager(context!!)

        // тут это не работает
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        this.adapter?.withSavedInstanceState(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        this.adapter?.saveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    /*override fun onStart() {
        super.onStart()
        if (this.fastAdapter.adapterItemCount == 0) {
            loadNext(1)
        }
    }*/

    override fun onDestroyView() {
        this.fastAdapter.clear()
        //   this.footerAdapter?.clear()
        super.onDestroyView()
    }

    @CallSuper
    override fun onRefresh() {
        this.swipeRefreshLayout?.isRefreshing = swipeRefreshSupported()
    }

    @MainThread
    @CallSuper
    protected open fun changeEmptyListState(isEmpty: Boolean) {
        if (isEmpty) {
            this.list?.visibility = View.GONE
            this.empty?.visibility = View.VISIBLE
        } else {
            this.list?.visibility = View.VISIBLE
            this.empty?.visibility = View.GONE
        }
    }

    /*@MainThread
    protected fun changeFootedState(show: Boolean) {
        if (show) {
            this.footerAdapter?.activate()
        } else {
            this.footerAdapter?.deactivate()
        }
    }*/

    @WorkerThread
    protected open fun fillAdapter(data: List<ModelData>?, more: Boolean) {
        this.swipeRefreshLayout?.post { this.swipeRefreshLayout?.isRefreshing = false }

        if (data != null && data.isNotEmpty()) {
            val items = ArrayList<ListItem?>(data.size)
            data.mapTo(items) { createListItemFromData(it) }

            this.view?.post {
                if (this.fastAdapter.adapterItemCount == 0) this.fastAdapter.setNewList(items)
                else this.fastAdapter.add(items)

                //TODO: добавить проверку на количество добавленных айтомов, если меньше чем видимо на экране - то надо убирать лоадер, иначе он остается
                //if (data.size < 7) {
                //    this.footerAdapter.clear()
                //}
            }
        } else {
            // this.list?.post { this.footerAdapter?.clear() }
        }

        this.list?.postDelayed({
            val empty = this.fastAdapter.adapterItemCount == 0
            changeEmptyListState(empty)

            //   if (!empty) changeFootedState(more && ((this.list?.layoutManager as? LinearLayoutManager)?.findLastVisibleItemPosition() ?: 0) < this.fastAdapter.adapterItemCount - 1)
        }, 250)
    }

    @AnyThread
    protected fun setItemsToAdapter(items: List<ListItem>) {
        this.list?.post { this.fastAdapter.setNewList(items) }
    }

    @MainThread
    protected open fun clean() {
        this.fastAdapter.clear()
        //   this.footerAdapter?.clear()
        //  loadNext(1)
        Log.e(javaClass.simpleName, "clean")
    }

    protected open fun getLayoutManager(c: Context): RecyclerView.LayoutManager = LinearLayoutManager(c)
    protected open fun swipeRefreshSupported() = false
    protected open fun clickListener(): com.mikepenz.fastadapter.listeners.OnClickListener<ListItem>? = null

    protected open fun createListItemFromData(item: ModelData): ListItem? = null

    @MainThread
    protected open fun installAdapter(l: RecyclerView, a: FastAdapter<ListItem>?) {
        l.adapter = a
    }

    private fun createAndGetAdapter(): FastAdapter<ListItem>? {
        //  this.footerAdapter = this.footerAdapter ?: LoadNextPageAdapter(this.toString())
        this.adapter = this.adapter
                ?: FastAdapter.with(Arrays.asList(this.headerAdapter, this.fastAdapter/*, this.footerAdapter*/))
        return this.adapter
    }
}
