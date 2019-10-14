package ui.activity

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem
import gromiloff.observer.EmptyBaseModel
import pro.gromiloff.mos.counters.R
import ui.BaseActivity
import java.util.*


class Main : BaseActivity<EmptyBaseModel>(), Drawer.OnDrawerItemClickListener {
    private var drawer: Drawer? = null
    private var finalHost: NavController? = null
    
    @LayoutRes
    override fun layoutId() = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.finalHost = Navigation.findNavController(this, R.id.menu_host_fragment)

        val items = ArrayList<IDrawerItem<*, *>>()
        items.add(PrimaryDrawerItem().withName("Статистика").withTag(R.id.menu_stats))
        items.add(PrimaryDrawerItem().withName("Настройки").withTag(R.id.menu_settings))
        
        this.drawer = DrawerBuilder(this)
            .withToolbar(this.toolbar!!)
            .withSliderBackgroundColorRes(R.color.colorPrimary)
            .withOnDrawerItemClickListener(this)
            .addDrawerItems(*items.toTypedArray())
            .build()    
    }

    override fun onBackPressed() {
        when {
            (this.drawer?.isDrawerOpen ?: false) -> this.drawer?.closeDrawer()
           // this.finalHost?.currentDestination?.id != R.id.menu_stats -> this.drawer?.setSelectionAtPosition(1)
            else -> super.onBackPressed()
        }
    }

    override fun onItemClick(view: View?, position: Int, drawerItem: IDrawerItem<*, *>): Boolean {
        when (drawerItem.tag is Int) {
            else -> this.finalHost?.navigate(drawerItem.tag as Int)
        }
        return false
    }
}
