package mosis.project.travelreport.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import mosis.project.travelreport.R
import mosis.project.travelreport.databinding.FragmentLoginBinding
import mosis.project.travelreport.ui.main.MainMenuActivity


class LoginFragment : Fragment() {


    //Binding
    private lateinit var binding:FragmentLoginBinding

    private lateinit var auth:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate( inflater, container, false)
        if(auth.currentUser != null)
        {
            var mainActivity = Intent(activity, MainMenuActivity::class.java)
            startActivity(mainActivity)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding.bLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val pass = binding.etPassword.text.toString()
            if(pass.isNotEmpty() && email.isNotEmpty())
            {
                auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                    if(it.isSuccessful)
                    {
                        var mainActivity = Intent(activity, MainMenuActivity::class.java)

                        startActivity(mainActivity)
                    }
                    else
                    {
                        Toast.makeText(this.context, it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }
            else
            {
                Toast.makeText(this.context, "Empty fields are not allowed", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tNotHaveAccount.setOnClickListener {
            findNavController().navigate(R.id.action_LoginFragment_to_RegisterFragment)
        }
    }




}