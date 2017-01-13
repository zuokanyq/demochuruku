package com.example.timingsystem;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import com.example.timingsystem.helper.DatabaseServer;
import com.example.timingsystem.model.InputBatch;
import com.example.timingsystem.model.Location;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;


/**
 * Created by zuokanyq on 2017-1-5.
 */

@RunWith(AndroidJUnit4.class)
@SmallTest
public class TestInput {

    private DatabaseServer inputServer;

    @Before
    public void createInputServer() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        inputServer = new DatabaseServer(appContext);
    }

    @Test
    public void insertInputBatch() {
        InputBatch inputBatch = new InputBatch();
        inputBatch.setBatchno("RE4332R09");
        inputBatch.setLocationList(new ArrayList<Location>());
        for (int i=0;i<3;i++){
            Location location =new Location();
            location.setLocationno("LocatNo"+String.valueOf(i));
            inputBatch.getLocationList().add(location);
        }
        long id= inputServer.createInputBatch(inputBatch);

        assertThat(id, is(notNullValue()));

        List<InputBatch> inputBatchList= inputServer.getInputBatchList();


        // Verify that the received data is correct.
        assertThat(inputBatchList.size(), is(1));
        assertThat(inputBatchList.get(0).getBatchno(), is("RE4332R09"));
        assertThat(inputBatchList.get(0).getLocationList().size(), is(3));

        inputServer.deleteInputBatch(inputBatchList);

        List<InputBatch> inputBatchListno= inputServer.getInputBatchList();
        assertThat(inputBatchListno.size(), is(0));
    }

}
