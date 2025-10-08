package com.triple7.healthshield254.data

import android.app.ProgressDialog
import android.content.Context
import android.widget.Toast
import androidx.navigation.NavController
import com.triple7.healthshield254.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.triple7.healthshield254.navigation.ROUT_HOME
import com.triple7.healthshield254.navigation.ROUT_LOGIN
import com.triple7.healthshield254.navigation.ROUT_REGISTER


class AuthViewModel(var navController: NavController, var context: Context){
    val mAuth: FirebaseAuth

    init {
        mAuth = FirebaseAuth.getInstance()
    }
    fun signup(username:String, email:String, password:String,confirmpassword:String){

        if (email.isBlank() || password.isBlank() ||confirmpassword.isBlank()){
            Toast.makeText(context,"Please email and password cannot be blank", Toast.LENGTH_LONG).show()
        }else if (password != confirmpassword){
            Toast.makeText(context,"Password do not match", Toast.LENGTH_LONG).show()
        }else{
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {
                if (it.isSuccessful){
                    val userdata= User(username, email, password, mAuth.currentUser!!.uid)
                    val regRef= FirebaseDatabase.getInstance().getReference()
                        .child("Users/"+mAuth.currentUser!!.uid)
                    regRef.setValue(userdata).addOnCompleteListener {

                        if (it.isSuccessful){
                            Toast.makeText(context,"Registered Successfully", Toast.LENGTH_LONG).show()
                            navController.navigate(ROUT_LOGIN)

                        }else{
                            Toast.makeText(context,"${it.exception!!.message}", Toast.LENGTH_LONG).show()
                            navController.navigate(ROUT_REGISTER)
                        }
                    }
                }else{
                    navController.navigate(ROUT_REGISTER)
                }

            } }

    }

    fun login(email: String, password: String){

        if (email.isBlank() || password.isBlank()){
            Toast.makeText(context,"Please email and password cannot be blank", Toast.LENGTH_LONG).show()
        }
        else {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful ){
                    Toast.makeText(this.context, "Success", Toast.LENGTH_SHORT).show()
                    navController.navigate(ROUT_HOME)
                }else{
                    Toast.makeText(this.context, "Error", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }




    fun logout(){
        mAuth.signOut()
    }

    fun isLoggedIn(): Boolean = mAuth.currentUser != null

}