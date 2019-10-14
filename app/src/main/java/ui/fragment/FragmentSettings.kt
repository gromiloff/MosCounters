package ui.fragment

import gromiloff.observer.EmptyBaseModel
import gromiloff.observer.impl.ObserverFragment
import pro.gromiloff.mos.counters.R

class FragmentSettings : ObserverFragment<EmptyBaseModel>() {
    override fun getModelClass() = EmptyBaseModel::class.java

    override fun layoutId() = R.layout.fragment_settings
}
