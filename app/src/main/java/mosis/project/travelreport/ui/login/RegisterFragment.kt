package mosis.project.travelreport.ui.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import mosis.project.travelreport.R
import mosis.project.travelreport.databinding.FragmentRegisterBinding


class RegisterFragment : Fragment() {

private  lateinit var binding: FragmentRegisterBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentRegisterBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.bRegister.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val pass = binding.etPassword.text.toString()
            val conformPass = binding.etRepeatPassword.text.toString()
            if(email.isNotEmpty() && pass.isNotEmpty() && conformPass.isNotEmpty())
            {
                if(pass.equals(conformPass))
                {
                    auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
                        if(it.isSuccessful)
                        {
                            findNavController().navigate(R.id.action_RegisterFragment_to_UserInfoFragment)

                        }
                        else
                        {
                            Toast.makeText(this.context, it.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                else
                {
                    Toast.makeText(this.context, "Passwords are not matching", Toast.LENGTH_SHORT).show()
                }
            }
            else
            {
                Toast.makeText(this.context, "Empty fields are not allowed", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tLogin.setOnClickListener {
            findNavController().navigate(R.id.action_RegisterFragment_to_LoginFragment)
        }
    }




}