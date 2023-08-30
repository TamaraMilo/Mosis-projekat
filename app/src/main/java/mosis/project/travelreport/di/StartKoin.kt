package mosis.project.travelreport.di

import mosis.project.travelreport.app.TravelReportApplication
import mosis.project.travelreport.di.modules.ViewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidFileProperties
import org.koin.core.context.startKoin

fun startKoin(application:TravelReportApplication ) {
    startKoin {
        androidContext(application)
        androidFileProperties()
        modules(ViewModelModule)
    }
}