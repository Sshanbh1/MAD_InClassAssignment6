package com.example.inclass06;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;



/*

Sameer Shanbhag
Ravina Gaikawad
Group Number 1 5

*/

public class MainActivity extends AppCompatActivity {

    String[] categories = {"Business" , "Entertainment", "General", "Health", "Science", "Sports", "Technology"};
    TextView tv_selectText;
    String keyword = "category";

    LinearLayout ll_loading;
    ProgressBar pb_progress;

    ImageView iv_prev;
    ImageView iv_next;

    TextView tv_description;
    TextView tv_title;
    ImageView imageView;
    TextView tv_numbers;
    TextView tv_published;

    ArrayList<News> NewsLoader = new ArrayList<>();
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Main Activity");
        tv_selectText = findViewById(R.id.tv_selectText);

        ll_loading = findViewById(R.id.ll_loading);
        pb_progress = findViewById(R.id.pb_progress);

        ll_loading.setVisibility(View.GONE);
        pb_progress.setVisibility(View.GONE);

        iv_prev = findViewById(R.id.iv_prev);
        iv_next = findViewById(R.id.iv_next);

        tv_description = findViewById(R.id.tv_description);
        tv_title = findViewById(R.id.tv_title);
        imageView = findViewById(R.id.imageView);
        tv_numbers = findViewById(R.id.tv_numbers);
        tv_published = findViewById(R.id.tv_published);

//        iv_next.setEnabled(false);
//        iv_prev.setEnabled(false);

        findViewById(R.id.bt_select).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCategories(categories);
            }
        });

        iv_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count + 1 <= NewsLoader.size() - 1){
                    count = count + 1;
                    tv_title.setText(NewsLoader.get(count).getTitle());
                    tv_description.setText(NewsLoader.get(count).getDescription());
                    Picasso.get().load(NewsLoader.get(count).getImage()).into(imageView);
                    tv_published.setText(NewsLoader.get(count).getPublished());
                    if(NewsLoader.size() > 20) {
                        tv_numbers.setText(count + 1 + " out of 20");
                    } else {
                        tv_numbers.setText(count + 1 +" out of " + NewsLoader.size());
                    }

                } else {
                    count = 0;
                    tv_title.setText(NewsLoader.get(count).getTitle());
                    tv_description.setText(NewsLoader.get(count).getDescription());
                    Picasso.get().load(NewsLoader.get(count).getImage()).into(imageView);
                    tv_published.setText(NewsLoader.get(count).getPublished());
                    if(NewsLoader.size() > 20) {
                        tv_numbers.setText(count + 1 +" out of 20");
                    } else {
                        tv_numbers.setText(count + 1 +" out of " + NewsLoader.size());
                    }

                }

            }
        });

        iv_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count == 0){
                    count = NewsLoader.size() - 1;
                    tv_title.setText(NewsLoader.get(count).getTitle());
                    tv_description.setText(NewsLoader.get(count).getDescription());
                    Picasso.get().load(NewsLoader.get(count).getImage()).into(imageView);
                    tv_published.setText(NewsLoader.get(count).getPublished());
                    if(NewsLoader.size() > 20) {
                        tv_numbers.setText(count + 1 +" out of 20");
                    } else {
                        tv_numbers.setText(count + 1 + " out of " + NewsLoader.size());
                    }
                } else {
                    count = count - 1;
                    tv_title.setText(NewsLoader.get(count).getTitle());
                    tv_description.setText(NewsLoader.get(count).getDescription());
                    Picasso.get().load(NewsLoader.get(count).getImage()).into(imageView);
                    tv_published.setText(NewsLoader.get(count).getPublished());
                    if(NewsLoader.size() > 20) {
                        tv_numbers.setText(count + 1 + " out of 20");
                    } else {
                        tv_numbers.setText(count + 1 + " out of " + NewsLoader.size());
                    }
                }

            }
        });
    }

    private boolean isConnected(){

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());

        if(networkCapabilities != null)
        {
            if(networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
            {
                return true;
            }
        }
        return false;
    }

    public void getCategories(final String[] categories) {
        this.categories = categories;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose a Keyword")
                .setItems(categories, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        RequestParams requestParams = new RequestParams();
                        requestParams.addParams(keyword, categories[i]);
                        tv_selectText.setText(categories[i]);
                        if(isConnected()){
                            new GetNewsAsyncTask(requestParams).execute("https://newsapi.org/v2/top-headlines?country=us");
                        } else {
                            Toast.makeText(MainActivity.this, "Internet Not connected", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private class GetNewsAsyncTask extends AsyncTask<String, Void, ArrayList<News>> {
        RequestParams requestParams;
        public GetNewsAsyncTask(RequestParams requestParams) {
            this.requestParams = requestParams;
        }

        @Override
        protected ArrayList<News> doInBackground(String... params) {
            HttpURLConnection connection = null;
            ArrayList<News> result = new ArrayList<>();
            try {
                Log.d("Get URL", requestParams.GetEncodedUrl(params[0]));
                URL url = new URL(requestParams.GetEncodedUrl(params[0]));
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    // Code for Getting the data
                    String json = IOUtils.toString(connection.getInputStream(), "UTF8");
                    JSONObject root = new JSONObject(json);
                    String status = root.getString("status");
                    int totalArticle = root.getInt("totalResults");
                    if (status.equals("ok") && totalArticle > 0) {
                        JSONArray articles = root.getJSONArray("articles");
                        for (int i=0;i<20;i++) {
                            JSONObject articleJson = articles.getJSONObject(i);
                            Log.d("Bagh 2", articleJson.toString());
                            News news1 = new News();
                            news1.title = articleJson.getString("title");
                            news1.published = articleJson.getString("publishedAt");
                            news1.description = articleJson.getString("description");
                            news1.image = articleJson.getString("urlToImage");
                            result.add(news1);
                        }
                    }
                    Log.d("Bagh 1", Arrays.toString(result.toArray()));
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(ArrayList<News> result) {
            if (result != null) {
                ll_loading.setVisibility(View.GONE);
                pb_progress.setVisibility(View.GONE);
//                iv_prev.setEnabled(true);
//                iv_next.setEnabled(true);
                tv_title.setText(result.get(0).getTitle());
                tv_description.setText(result.get(0).getDescription());
                Picasso.get().load(result.get(0).getImage()).into(imageView);
                tv_published.setText(result.get(0).getPublished());
                tv_numbers.setText("1 out of 20");

                NewsLoader = result;

            } else {
                Toast.makeText(MainActivity.this, "No News Found", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            ll_loading.setVisibility(View.VISIBLE);
            pb_progress.setVisibility(View.VISIBLE);
        }
    }
}
