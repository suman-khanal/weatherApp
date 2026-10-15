package au.edu.jcu.assessment.utilityapp

import android.app.Application
import au.edu.jcu.assessment.utilityapp.di.AppContainer

class DayReadyApplication : Application() {
    val container by lazy { AppContainer() }
}
