package com.mifos.mifosxdroid;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.mifos.mifosxdroid.adapters.ClientNameListAdapter;
import com.mifos.objects.client.Client;
import com.mifos.objects.client.Page;
import com.mifos.objects.client.PageItem;

import java.util.List;

import com.mifos.utils.services.API;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by ishankhanna on 09/02/14.
 */
public class ClientListFragment extends Fragment {


    ListView lv_clients;

    View rootView;

    List<Client> pageItems;
    FragmentChangeListener activityListener;
    private Context context;

    public ClientListFragment() {

    }


    public static interface FragmentChangeListener {
        void replaceFragments(Fragment fragment);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_client, container, false);


        context = getActivity().getApplicationContext();
        activityListener = (FragmentChangeListener) getActivity();

        setupUI();

        API.clientService.listAllClients(new Callback<Page>() {
            @Override
            public void success(Page page, Response response) {
                pageItems = page.getPageItems();

                ClientNameListAdapter clientNameListAdapter = new ClientNameListAdapter(context, pageItems);
                lv_clients.setAdapter(clientNameListAdapter);
                lv_clients.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                        ClientDetailsFragment clientDetailsFragment =
                                ClientDetailsFragment.newInstance(pageItems.get(i).getId());
                        activityListener.replaceFragments(clientDetailsFragment);


                    }
                });
            }

            @Override
            public void failure(RetrofitError retrofitError) {

                if(getActivity() != null)
                    Toast.makeText(getActivity(), "There was some error fetching list.", Toast.LENGTH_SHORT).show();

            }
        });


        return rootView;
    }

    public void setupUI() {

        setHasOptionsMenu(true);

        lv_clients = (ListView) rootView.findViewById(R.id.lv_clients);

        lv_clients.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                if(getActivity() != null)
                Toast.makeText(getActivity(), "Client ID = " + pageItems.get(i).getId(), Toast.LENGTH_SHORT).show();

                return false;
            }
        });


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.dashbord_client, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.mItem_search:
                activityListener.replaceFragments(new ClientSearchFragment());
                break;

            default: //DO NOTHING
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
