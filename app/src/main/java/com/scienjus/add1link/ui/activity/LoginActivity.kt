package com.scienjus.add1link.ui.activity

import LoginMutation
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.scienjus.add1link.R
import com.scienjus.add1link.client.AccountStorage
import com.scienjus.add1link.client.ApolloHttpClient
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        login_progress_bar.visibility = View.INVISIBLE
    }

    fun login(view: View) {
        val email = login_email.text.toString()
        val password = login_password.text.toString()
        login_progress_bar.visibility = View.VISIBLE

        ApolloHttpClient.instance.mutate(LoginMutation.builder()
                .email(email)
                .password(password)
                .build())
                .enqueue(object : ApolloCall.Callback<LoginMutation.Data>() {
                    override fun onFailure(e: ApolloException) {
                        login_progress_bar.visibility = View.INVISIBLE
                        Log.v(this.javaClass.simpleName, "error is: ${e.message.toString()}")
                    }
                    override fun onResponse(response: Response<LoginMutation.Data>) {
                        login_progress_bar.visibility = View.INVISIBLE
                        response.data()?.login()?.token()?.let { token ->
                            Log.v(this.javaClass.simpleName, "token is: $token")
                            AccountStorage.put(AccountStorage.Account(email, token))
                            setResult(RESULT_OK)
                            finish()
                        }
                    }
                })
    }
}
