package com.example.timingsystem;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import com.example.timingsystem.helper.DatabaseServer;
import com.example.timingsystem.model.InputBatch;
import com.example.timingsystem.model.Location;
import com.example.timingsystem.model.OutputBatch;

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

  //  @Test
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

  //  @Test
    public void insertOutputBatch() {
        OutputBatch outputBatch = new OutputBatch();
        outputBatch.setBatchno("RE4332R09");
        outputBatch.setLocationList(new ArrayList<Location>());
        for (int i=0;i<3;i++){
            Location location =new Location();
            location.setLocationno("LocatNo"+String.valueOf(i));
            outputBatch.getLocationList().add(location);
        }
        long id= inputServer.createOutputBatch(outputBatch);

        assertThat(id, is(notNullValue()));

        List<OutputBatch> outputBatchList= inputServer.getOutputBatchList();


        // Verify that the received data is correct.
        assertThat(outputBatchList.size(), is(1));
        assertThat(outputBatchList.get(0).getBatchno(), is("RE4332R09"));
        assertThat(outputBatchList.get(0).getLocationList().size(), is(3));

        inputServer.deleteOutputBatch(outputBatchList);

        List<OutputBatch> outputBatchListno= inputServer.getOutputBatchList();
        assertThat(outputBatchListno.size(), is(0));
    }

    @Test
    public void deletelist(){
        List<InputBatch> inputBatchList= inputServer.getInputBatchList();
        inputServer.deleteInputBatch(inputBatchList);
        List<InputBatch> inputBatchList0= inputServer.getInputBatchList();
        assertThat(inputBatchList0.size(), is(0));

        InputBatch inputBatch = new InputBatch();
        inputBatch.setBatchno("RE4332");
        inputBatch.setLocationList(new ArrayList<Location>());
        for (int i=0;i<3;i++){
            Location location =new Location();
            location.setLocationno("LocatNo"+String.valueOf(i));
            inputBatch.getLocationList().add(location);
        }
        long id= inputServer.createInputBatch(inputBatch);

        assertThat(id, is(notNullValue()));
        long id1= inputServer.createInputBatch(inputBatch);
        inputBatch.setBatchno("RE4333");
        inputServer.createInputBatch(inputBatch);


        List<InputBatch> inputBatchList1= inputServer.getInputBatchList();
        assertThat(inputBatchList1.size(), is(3));

        List<String> batchNOlist= new ArrayList<String>();
        batchNOlist.add("RE4332");
        batchNOlist.add("RE4333");
        inputServer.deleteInputBatchByNo(batchNOlist);

        List<InputBatch> inputBatchList2= inputServer.getInputBatchList();
        assertThat(inputBatchList2.size(), is(0));

    }

}
