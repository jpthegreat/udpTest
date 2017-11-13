package kr.re.keti.udptest;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendForm;
import com.github.mikephil.charting.components.Legend.LegendPosition;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;


//public class MainActivity extends AppCompatActivity {
public class MainActivity extends AppCompatActivity implements OnSeekBarChangeListener, OnChartValueSelectedListener
{

    Button buttonConnect;
    EditText editTextAddress, editTextPort;
    TextView textViewState;
    ListView listView;
    List<String> list;
    ArrayAdapter<String> adapter;

    UdpClientHandler udpClientHandler;
    UdpClientThread udpClientThread;

    int PORT = 8888;
    String ADDR = "224.0.0.2";

    // from MPAndroid
    private LineChart mChart;
/*    private SeekBar mSeekBarX, mSeekBarY;
    private TextView tvX, tvY;*/

    ArrayList<Entry> yVals1 = new ArrayList<Entry>();
    ArrayList<Entry> yVals2 = new ArrayList<Entry>();
    ArrayList<Entry> yVals3 = new ArrayList<Entry>();
    ArrayList<Entry> yVals4 = new ArrayList<Entry>();
    int arrIdx = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*// original
        setContentView(R.layout.activity_main);

        editTextAddress = (EditText) findViewById(R.id.address);
        editTextPort = (EditText) findViewById(R.id.port);
        buttonConnect = (Button)findViewById(R.id.connect);
        textViewState = (TextView)findViewById(R.id.state);

        listView = (ListView)findViewById(R.id.received);
        list = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);

        buttonConnect.setOnClickListener(buttonConnectOnClickListener);

        udpClientHandler = new UdpClientHandler(this);*/


        // MPChart Library
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_linechart);

        buttonConnect = (Button)findViewById(R.id.connect);
        buttonConnect.setOnClickListener(buttonConnectOnClickListener);
        udpClientHandler = new UdpClientHandler(this);

        /*tvX = (TextView) findViewById(R.id.tvXMax);
        tvY = (TextView) findViewById(R.id.tvYMax);
        mSeekBarX = (SeekBar) findViewById(R.id.seekBar1);
        mSeekBarY = (SeekBar) findViewById(R.id.seekBar2);

        mSeekBarX.setProgress(45);
        mSeekBarY.setProgress(100);

        mSeekBarY.setOnSeekBarChangeListener(this);
        mSeekBarX.setOnSeekBarChangeListener(this);*/

        mChart = (LineChart) findViewById(R.id.chart1);
        mChart.setOnChartValueSelectedListener(this);

        // no description text
        //mChart.setDescription("");
        mChart.setNoDataText("You need to provide data for the chart.");
        //mChart.setNoDataTextDescription("You need to provide data for the chart.");

        // enable touch gestures
        mChart.setTouchEnabled(true);

        mChart.setDragDecelerationFrictionCoef(0.9f);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);
        mChart.setHighlightPerDragEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        // set an alternative background color
        mChart.setBackgroundColor(Color.BLACK);

        // add data
        //setData(20, 30);

        mChart.animateX(1500);

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        // l.setPosition(LegendPosition.LEFT_OF_CHART);
        l.setForm(LegendForm.LINE);
        //l.setTypeface(mTfLight);
        l.setTextSize(11f);
        l.setTextColor(Color.WHITE);
        l.setPosition(LegendPosition.BELOW_CHART_LEFT);
//        l.setYOffset(11f);

        XAxis xAxis = mChart.getXAxis();
        //xAxis.setTypeface(mTfLight);
        xAxis.setTextSize(11f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);

        YAxis leftAxis = mChart.getAxisLeft();
        //leftAxis.setTypeface(mTfLight);
        leftAxis.setTextColor(ColorTemplate.getHoloBlue());
        leftAxis.setAxisMaxValue(5000); //original val 0,200f
        leftAxis.setAxisMinValue(-1000);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularityEnabled(true);
/*
        YAxis rightAxis = mChart.getAxisRight();
        //rightAxis.setTypeface(mTfLight);
        rightAxis.setTextColor(Color.RED);
        rightAxis.setAxisMaxValue(900);
        rightAxis.setAxisMinValue(-200);
        rightAxis.setDrawGridLines(false);
        rightAxis.setDrawZeroLine(false);
        rightAxis.setGranularityEnabled(false);*/
    }

    View.OnClickListener buttonConnectOnClickListener =
            new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {

                    /*udpClientThread = new UdpClientThread(
                            editTextAddress.getText().toString(),
                            Integer.parseInt(editTextPort.getText().toString()),
                            udpClientHandler);*/
                    //udpClientThread = new UdpClientThread("224.0.0.2", 8888, udpClientHandler);
                    udpClientThread = new UdpClientThread(ADDR, PORT, udpClientHandler);
                    udpClientThread.start();

                    buttonConnect.setEnabled(false);
                }
            };

    // from MPAndroid


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

/*        tvX.setText("" + (mSeekBarX.getProgress() + 1));
        tvY.setText("" + (mSeekBarY.getProgress()));*/

        //setData(mSeekBarX.getProgress() + 1, mSeekBarY.getProgress());

        // redraw
        //mChart.invalidate();
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i("Entry selected", e.toString());

        mChart.centerViewToAnimated(e.getX(), e.getY(), mChart.getData().getDataSetByIndex(h.getDataSetIndex())
                .getAxisDependency(), 500);
        //mChart.zoomAndCenterAnimated(2.5f, 2.5f, e.getX(), e.getY(), mChart.getData().getDataSetByIndex(dataSetIndex)
        // .getAxisDependency(), 1000);
        //mChart.zoomAndCenterAnimated(1.8f, 1.8f, e.getX(), e.getY(), mChart.getData().getDataSetByIndex(dataSetIndex)
        // .getAxisDependency(), 1000);
    }

    @Override
    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }


    private void updateState(String state){
        //textViewState.setText(state);
        Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
    }

    private void updateRxMsg(byte[] rxmsg){
        //textViewRx.append(rxmsg + "\n");
        // parsing data
        byte[] rcvData = new byte[50];

        //for(int i =0; i<rxmsg.length; i++)    // length: 100
        for(int i =0; i<50; i++)
            Log.d("MAIN", "rxmsg["+i+"]: "+rxmsg[i]);

        System.arraycopy(rxmsg, 0, rcvData, 0, 50); // copy 50 Bytes from rxmsg

        // prepare buffer
        byte[] node = new byte[4];      // 4B
        byte[] seqc = new byte[2];      // 2B
        byte[] data1 = new byte[16];    // 2B*8
        byte[] data2 = new byte[16];    // 2B*8
        byte[] data3 = new byte[10];    // 2B*5
        byte[] reversed = new byte[1];  // 1B

        // copy received data to buffer
        System.arraycopy(rcvData, 0, node, 0, 4);
        System.arraycopy(rcvData, 4, seqc, 0, 2);
        System.arraycopy(rcvData, 6, data1, 0, 16);
        System.arraycopy(rcvData,22, data2, 0, 16);
        System.arraycopy(rcvData,38, data3, 0, 10);
        System.arraycopy(rcvData,48, reversed, 0, 1);   // total 50B

        // convert data bytes to Str, Int respectively
        String nodStr;                                        // node(4)
        int seqInt = 0; int rvsInt = 0;                       // seqNo(2), reserved(2)
        int[] spiData = new int[]{0, 0, 0, 0, 0, 0, 0, 0};    // spi_data(16)
        int[] i2cData = new int[]{0, 0, 0, 0, 0, 0, 0, 0};    // i2c_data(16)
        int[] dataInt = new int[]{0, 0, 0, 0, 0};             // rest(10)

        // node
        nodStr = new String(node, 0, node.length);

        // packet sequence number
        seqInt |= ( (((int)seqc[0]) & 0xff ) << 8 ) | ( (int)seqc[1] & 0xff ); // little endian

        // spi_data
        spiData[0] |= ( (((int)data1[0]) & 0xff ) << 8) | ( (int)data1[1] & 0xff );
        spiData[1] |= ( (((int)data1[2]) & 0xff ) << 8) | ( (int)data1[3] & 0xff );
        spiData[2] |= ( (((int)data1[4]) & 0xff ) << 8) | ( (int)data1[5] & 0xff );
        spiData[3] |= ( (((int)data1[6]) & 0xff ) << 8) | ( (int)data1[7] & 0xff );
        spiData[4] |= ( (((int)data1[8]) & 0xff ) << 8) | ( (int)data1[9] & 0xff );
        spiData[5] |= ( (((int)data1[10]) & 0xff ) << 8) | ( (int)data1[11] & 0xff );
        spiData[6] |= ( (((int)data1[12]) & 0xff ) << 8) | ( (int)data1[13] & 0xff );
        spiData[7] |= ( (((int)data1[14]) & 0xff ) << 8) | ( (int)data1[15] & 0xff );

        // i2c_data
        i2cData[0] |= ( (((int)data2[0]) & 0xff ) << 8) | ( (int)data2[1] & 0xff );
        i2cData[1] |= ( (((int)data2[2]) & 0xff ) << 8) | ( (int)data2[3] & 0xff );
        i2cData[2] |= ( (((int)data2[4]) & 0xff ) << 8) | ( (int)data2[5] & 0xff );
        i2cData[3] |= ( (((int)data2[6]) & 0xff ) << 8) | ( (int)data2[7] & 0xff );
        i2cData[4] |= ( (((int)data2[8]) & 0xff ) << 8) | ( (int)data2[9] & 0xff );
        i2cData[5] |= ( (((int)data2[10]) & 0xff ) << 8) | ( (int)data2[11] & 0xff );
        i2cData[6] |= ( (((int)data2[12]) & 0xff ) << 8) | ( (int)data2[13] & 0xff );
        i2cData[7] |= ( (((int)data2[14]) & 0xff ) << 8) | ( (int)data2[15] & 0xff );

        // rest data
        dataInt[0] |= ( (((int)data3[0]) & 0xff ) << 8) | ( (int)data3[1] & 0xff );
        dataInt[1] |= ( (((int)data3[2]) & 0xff ) << 8) | ( (int)data3[3] & 0xff );
        dataInt[2] |= ( (((int)data3[4]) & 0xff ) << 8) | ( (int)data3[5] & 0xff );
        dataInt[3] |= ( (((int)data3[6]) & 0xff ) << 8) | ( (int)data3[7] & 0xff );
        dataInt[4] |= ( (((int)data3[8]) & 0xff ) << 8) | ( (int)data3[9] & 0xff );

        // reserved byte
        rvsInt |= ((int)reversed[0]) & 0xff;

        /*// show data - text
        StringBuilder output = new StringBuilder();
        output.append(nodStr+" ");
        output.append("sq: " + Integer.toString(seqInt) + " ");
        for(int i=0; i<8; i++){
            //output.append("spiData[" + i + "]: " + Integer.toString(spiData[i]) + " ");
            output.append(Integer.toString(spiData[i]) + " ");
        }
        output.append(" / ");
        for(int i=0; i<8; i++){
            output.append(Integer.toString(i2cData[i]) + " ");
        }
        output.append(" / ");
        for(int i=0; i<5; i++){
            output.append(Integer.toString(dataInt[i]) + " ");
        }
        output.append(" / ");
        output.append(Integer.toString(rvsInt));

        list.add(output.toString());
        adapter.notifyDataSetChanged();
        listView.setSelection(adapter.getCount()-1);    // scroll to End*/

        showData(spiData);  // test

    }

    private void showData(int[] dataVal){

        int val1 = dataVal[0];
        int val2 = dataVal[1];
        int val3 = dataVal[2];
        int val4 = dataVal[3];

        yVals1.add(new Entry(arrIdx, val1));
        yVals2.add(new Entry(arrIdx, val2));
        yVals3.add(new Entry(arrIdx, val3));
        yVals4.add(new Entry(arrIdx++, val4));

        LineDataSet set1, set2, set3, set4;

        if (mChart.getData() != null && mChart.getData().getDataSetCount() > 0)
        {
            set1 = (LineDataSet) mChart.getData().getDataSetByIndex(0);
            set2 = (LineDataSet) mChart.getData().getDataSetByIndex(1);
            set3 = (LineDataSet) mChart.getData().getDataSetByIndex(2);
            set4 = (LineDataSet) mChart.getData().getDataSetByIndex(3);

            set1.setValues(yVals1);
            set2.setValues(yVals2);
            set3.setValues(yVals3);
            set4.setValues(yVals4);

            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();

        }
        else
        {
            // create a dataset and give it a type
            set1 = new LineDataSet(yVals1, "SPIData[0]");
            set1.setAxisDependency(AxisDependency.LEFT);
            set1.setColor(ColorTemplate.getHoloBlue());
            //set1.setCircleColor(Color.WHITE);
            set1.setLineWidth(1f);
            set1.setDrawCircleHole(false);
            set1.setCircleRadius(0f);
            //set1.setFillAlpha(65);
            //set1.setFillColor(ColorTemplate.getHoloBlue());
            //set1.setHighLightColor(Color.rgb(244, 117, 117));

            //set1.setFillFormatter(new MyFillFormatter(0f));
            //set1.setDrawHorizontalHighlightIndicator(false);
            //set1.setVisible(false);
            //set1.setCircleHoleColor(Color.WHITE);

            // create a dataset and give it a type
            set2 = new LineDataSet(yVals2, "SPIData[1]");
            set2.setAxisDependency(YAxis.AxisDependency.LEFT);
            set2.setColor(Color.YELLOW);
            //set2.setCircleColor(Color.WHITE);
            set2.setLineWidth(1f);
            set2.setDrawCircleHole(false);
            set2.setCircleRadius(0f);
            //set2.setFillAlpha(65);
            //set2.setFillColor(Color.RED);

            //set2.setHighLightColor(Color.rgb(244, 117, 117));
            //set2.setFillFormatter(new MyFillFormatter(900f));

            // create a dataset and give it a type
            set3 = new LineDataSet(yVals2, "SPIData[2]");
            set3.setAxisDependency(YAxis.AxisDependency.LEFT);
            set3.setColor(Color.GREEN);
            //set3.setCircleColor(Color.WHITE);
            set3.setLineWidth(1f);
            set3.setDrawCircleHole(false);
            set3.setCircleRadius(0f);
            //set3.setFillAlpha(65);
            //set3.setFillColor(Color.RED);

            //set3.setHighLightColor(Color.rgb(244, 117, 117));

            // create a dataset and give it a type
            set4 = new LineDataSet(yVals2, "SPIData[3]");
            set4.setAxisDependency(YAxis.AxisDependency.LEFT);
            set4.setColor(Color.RED);
            //set4.setCircleColor(Color.WHITE);
            set4.setLineWidth(1f);
            set4.setDrawCircleHole(false);
            set4.setCircleRadius(0f);
            //set4.setFillAlpha(65);
            //set4.setFillColor(Color.RED);
            //set4.setHighLightColor(Color.rgb(244, 117, 117));
            //set2.setFillFormatter(new MyFillFormatter(900f));

            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(set1); // add the datasets
            dataSets.add(set2);
            dataSets.add(set3);
            dataSets.add(set4);

            // create a data object with the datasets
            LineData data = new LineData(dataSets);
            //data.setValueTextColor(Color.WHITE);
            data.setValueTextSize(9f);

            // set data
            mChart.setData(data);
        }
        // redraw
        mChart.invalidate();
    }

    private void clientEnd(){
        udpClientThread = null;
        //textViewState.setText("clientEnd()");
        buttonConnect.setEnabled(true);

    }

    public static class UdpClientHandler extends Handler {
        public static final int UPDATE_STATE = 0;
        public static final int UPDATE_MSG = 1;
        public static final int UPDATE_END = 2;
        private MainActivity parent;

        public UdpClientHandler(MainActivity parent) {
            super();
            this.parent = parent;
        }

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what){
                case UPDATE_STATE:
                    parent.updateState((String)msg.obj);
                    break;
                case UPDATE_MSG:
                    //parent.updateRxMsg((String)msg.obj);
                    parent.updateRxMsg((byte[])msg.obj);
                    break;
                case UPDATE_END:
                    parent.clientEnd();
                    break;
                default:
                    super.handleMessage(msg);
            }

        }
    }

}
