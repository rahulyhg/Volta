package com.jmjsolution.solarup.services.emailService;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.jmjsolution.solarup.ui.activities.HomeActivity;
import com.sun.mail.smtp.SMTPTransport;
import com.sun.mail.util.BASE64EncoderStream;

import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.URLName;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;

import static com.jmjsolution.solarup.utils.Constants.ACCOUNT_TYPE;
import static com.jmjsolution.solarup.utils.Constants.GMAIL;

public class GmailService {

    private SharedPreferences sharedPref;
    private Context context;
    private AccountManager acctManager;
    private String token;
    private Session session;
    private String body;
    private String receiver;
    private String title;
    private View view;


    public GmailService(Context context, String body, String receiver, String title, View view, SharedPreferences sharedPref){
        this.context = context;
        this.body = body;
        this.receiver = receiver;
        this.title = title;
        this.sharedPref = sharedPref;
        this.view = view;
        acctManager = AccountManager.get(context);
    }


    public void initToken() {
        acctManager.getAuthToken(createAccountFromData(), "oauth2:https://mail.google.com/", null, (Activity) context, new AccountManagerCallback<Bundle>(){
            @Override
            public void run(AccountManagerFuture<Bundle> result){
                try{
                    Bundle bundle = result.getResult();
                    token = bundle.getString(AccountManager.KEY_AUTHTOKEN);
                    getAndUseAuthTokenInAsyncTask().execute();
                    Log.d("initToken callback", "token="+token);

                } catch (Exception e){
                    Log.d("test", e.getMessage());
                }
            }
        }, null);

    }

    @SuppressLint("StaticFieldLeak")
    private AsyncTask getAndUseAuthTokenInAsyncTask() {
        return new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                sendMail(title, body, createAccountFromData().name, token, receiver);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                if(view != null){
                    view.setVisibility(View.GONE);
                    Toast.makeText(context, "Email envoyé", Toast.LENGTH_SHORT).show();
                    context.startActivity(new Intent(context, HomeActivity.class));
                }
            }
        };
    }

    private synchronized void sendMail (String subject, String body, String user, String oauthToken, String recipients) { try {

        SMTPTransport smtpTransport = connectToSmtp("smtp.gmail.com", 587,
                user, oauthToken, true);

        MimeMessage message = new MimeMessage(session);
        DataHandler handler = new DataHandler(new ByteArrayDataSource(
                body.getBytes(), "text/plain"));
        message.setSender(new InternetAddress(user));
        message.setSubject(subject);
        message.setDataHandler(handler);
        if (recipients.indexOf(',') > 0)
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(recipients));
        else
            message.setRecipient(Message.RecipientType.TO,
                    new InternetAddress(recipients));
        smtpTransport.sendMessage(message, message.getAllRecipients());

    } catch (Exception e) {
        Log.d("test", e.getMessage(), e);
    }
    }

    private SMTPTransport connectToSmtp(String host, int port, String userEmail, String oauthToken, boolean debug) throws Exception {

        Properties props = new Properties();
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.sasl.enable", "false");

        session = Session.getInstance(props);
        session.setDebug(debug);

        final URLName unusedUrlName = null;
        SMTPTransport transport = new SMTPTransport(session, unusedUrlName);
        // If the password is non-null, SMTP tries to do AUTH LOGIN.
        final String emptyPassword = null;

        transport.connect(host, port, userEmail, emptyPassword);

        byte[] response = String.format("user=%s\1auth=Bearer %s\1\1", userEmail, oauthToken).getBytes();
        response = BASE64EncoderStream.encode(response);

        transport.issueCommand("AUTH XOAUTH2 " + new String(response), 235);

        return transport;
    }

    private Account createAccountFromData(){
        return new Account(sharedPref.getString(GMAIL, null), sharedPref.getString(ACCOUNT_TYPE, null));
    }


}
