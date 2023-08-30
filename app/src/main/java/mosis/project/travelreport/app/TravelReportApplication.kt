package mosis.project.travelreport.app

import android.app.Application
import mosis.project.travelreport.di.startKoin
import mosis.project.travelreport.location.LocationWizard


class TravelReportApplication : Application() {
    init {
        instance = this
    }

    companion object {
        private var instance: TravelReportApplication? = null

        fun applicationContext(): TravelReportApplication {
            return instance as TravelReportApplication
        }
    }

    override fun onCreate() {
        super.onCreate()
        startKoin(applicationContext())
        LocationWizard()
    }
}