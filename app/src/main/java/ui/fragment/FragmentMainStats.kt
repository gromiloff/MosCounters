package ui.fragment

import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.lifecycle.ViewModel
import api.EmptyModelResponse
import api.RequestModel2
import api.impl.IArchiveRequest
import gromiloff.observer.EmptyBaseModel
import gromiloff.observer.ParserFullImpl
import gromiloff.observer.event.LoadingStart
import gromiloff.observer.impl.ObserverFragment
import pro.gromiloff.mos.counters.R

/**
 * основнаялогика следующая - получаем историческую инфу за дооолгий период, обрабатываем и отображаем необходимые данные тут
 * 
 * отображаем в 2 колонки (счетчики как правило идут в паре)
 * */
class FragmentMainStats : ObserverFragment<EmptyBaseModel>() {
    override fun getModelClass() = EmptyBaseModel::class.java

    override fun layoutId() = R.layout.fragment_stats

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        LoadingStart().send()
    }
}


class MainStatsModel : EmptyModelResponse(), IArchiveRequest {
    override var counterId: Long = 0L
    override var startPeriod: String = ""
    override var endPeriod: String = ""
    override val paycode: Long = 0L
    override val flat: String = ""

    override fun success(it: RequestModel2?, response: Any?) {
    }
}
