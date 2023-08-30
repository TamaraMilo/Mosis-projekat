package mosis.project.travelreport.di.modules

import mosis.project.travelreport.model.CommentViewModel
import mosis.project.travelreport.model.LocationViewModel
import mosis.project.travelreport.model.UserViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val ViewModelModule = module {
    viewModel{UserViewModel()}
    viewModel{CommentViewModel()}
}