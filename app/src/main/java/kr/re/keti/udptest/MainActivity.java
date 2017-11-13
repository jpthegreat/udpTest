package kr.re.keti.udptest;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button buttonConnect;
    EditText editTextAddress, editTextPort;
    TextView textViewState;
    ListView listView;
    List<String> list;
    ArrayAdapter<String> adapter;

    UdpClientHandler udpClientHandler;
    UdpClientThread udpClientThread;


    /* from MPAndroid
    private LineChart mChart;
    private SeekBar mSeekBarX, mSeekBarY;
    private TextView tvX, tvY;
    */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        udpClientHandler = new UdpClientHandler(this);

        /*

        // MPChart Library
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_linechart);

        tvX = (TextView) findViewById(R.id.tvXMax);
        tvY = (TextView) findViewById(R.id.tvYMax);
        mSeekBarX = (SeekBar) findViewById(R.id.seekBar1);
        mSeekBarY = (SeekBar) findViewById(R.id.seekBar2);

        mSeekBarX.setProgress(45);
        mSeekBarY.setProgress(100);

        mSeekBarY.setOnSeekBarChangeListener(this);
        mSeekBarX.setOnSeekBarChangeListener(this);

        mChart = (LineChart) findViewById(R.id.chart1);
        mChart.setOnChartValueSelectedListener(this);

        // no description text
        mChart.setDescription("");
        mChart.setNoDataTextDescription("You need to provide data for the chart.");

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
        mChart.setBackgroundColor(Color.LTGRAY);

        // add data
        setData(20, 30);

        mChart.animateX(2500);

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        // l.setPosition(LegendPosition.LEFT_OF_CHART);
        l.setForm(LegendForm.LINE);
        l.setTypeface(mTfLight);
        l.setTextSize(11f);
        l.setTextColor(Color.WHITE);
        l.setPosition(LegendPosition.BELOW_CHART_LEFT);
//        l.setYOffset(11f);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setTypeface(mTfLight);
        xAxis.setTextSize(11f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTypeface(mTfLight);
        leftAxis.setTextColor(ColorTemplate.getHoloBlue());
        leftAxis.setAxisMaxValue(200f);
        leftAxis.setAxisMinValue(0f);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularityEnabled(true);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setTypeface(mTfLight);
        rightAxis.setTextColor(Color.RED);
        rightAxis.setAxisMaxValue(900);
        rightAxis.setAxisMinValue(-200);
        rightAxis.setDrawGridLines(false);
        rightAxis.setDrawZeroLine(false);
        rightAxis.setGranularityEnabled(false);
        */
    }

    View.OnClickListener buttonConnectOnClickListener =
            new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {

                    udpClientThread = new UdpClientThread(
                            editTextAddress.getText().toString(),
                            Integer.parseInt(editTextPort.getText().toString()),
                            udpClientHandler);
                    udpClientThread.start();

                    buttonConnect.setEnabled(false);
                }
            };

    private void updateState(String state){
        textViewState.setText(state);
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

        // show data
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
        listView.setSelection(adapter.getCount()-1);    // scroll to End

    }

    private void clientEnd(){
        udpClientThread = null;
        textViewState.setText("clientEnd()");
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
