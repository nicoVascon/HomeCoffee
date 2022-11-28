package ipleiria.pdm.homecoffee.ui.home;

import androidx.fragment.app.Fragment;

public class ListHomeFragment extends Fragment {



/*

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
    }

    private void sendRequest() {
        final ProgressDialog loading = ProgressDialog.show(this, "Uploading...", "Please wait...", false, false);

        StringRequest stringRequest = new StringRequest(LIST_USER_URL, response -> {
            showJSON(response);
            loading.dismiss();
        }, error -> Toast.makeText(UserList.this, error.getMessage(), Toast.LENGTH_LONG).show());

        int socketTimeout = 30000; // 30 seconds. You can change it
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        stringRequest.setRetryPolicy(policy);


        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    private void sendRequestDelete(int i) {

    }

    private void showJSON(String json) {
        JsonParser pj = new JsonParser(json);
        pj.parseJSON();
       //TODO
    }


 */
}