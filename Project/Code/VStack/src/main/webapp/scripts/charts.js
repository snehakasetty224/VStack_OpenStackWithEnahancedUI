 function drawChart() {

	 drawTypeChart();
	 drawStatusChart();
	 drawServiceHealthChart1();
	 drawServiceHealthChart2();
	 drawBar();
}
 
 
function drawTypeChart() {
    // Create the data table.
    var data = new google.visualization.DataTable();
    data.addColumn('string', 'Topping');
    data.addColumn('number', 'Slices');
    data.addRows([
      ['Static', 1],
      ['Dynamic', 1]
    ]);

    // Set chart options
    var options = {'title':'Instance Type',
                   'width':400,
                   'height':300,
                   'pieHole': 0.4,colors: ['#e0440e', '#f3b49f', '#f6c7b6'],
                   backgroundColor: { fill:'transparent' }
};

    // Instantiate and draw our chart, passing in some options.
    var chart = new google.visualization.PieChart(document.getElementById('typeChart'));
    chart.draw(data, options);
    
	
}


function drawStatusChart() {
	 // Create the data table.
    var data1 = new google.visualization.DataTable();
    data1.addColumn('string', 'Topping');
    data1.addColumn('number', 'Slices');
    data1.addRows([
      ['Running', 2]
    ]);

    // Set chart options
    var options1 = {'title':'Service Health',
                   'width':400,
                   'height':300,
                   'pieHole': 0.4,
                  // colors: ['#e0440e', '#f3b49f', '#f6c7b6'],
                   backgroundColor: { fill:'transparent' }};

    // Instantiate and draw our chart, passing in some options.
    var chart1 = new google.visualization.PieChart(document.getElementById('statusChart'));
    chart1.draw(data1, options1);
	
}


function drawServiceHealthChart1() {
	 // Create the data table.
    var data1 = new google.visualization.DataTable();
    data1.addColumn('string', 'Topping');
    data1.addColumn('number', 'Slices');
    data1.addRows([
      ['Running', 1]
    ]);

    // Set chart options
    var options1 = {'title':'Service Health for Apache2',
                   'width':400,
                   'height':300,
                   'pieHole': 0.4,
                   //colors: ['#e0440e', '#f3b49f', '#f6c7b6'],
                   backgroundColor: { fill:'transparent' }};

    // Instantiate and draw our chart, passing in some options.
    var chart1 = new google.visualization.PieChart(document.getElementById('healthChart1'));
    chart1.draw(data1, options1);
	
}


function drawServiceHealthChart2() {
	 // Create the data table.
    var data1 = new google.visualization.DataTable();
    data1.addColumn('string', 'Topping');
    data1.addColumn('number', 'Slices');
    data1.addRows([
      ['Running', 1]
    ]);

    // Set chart options
    var options1 = {'title':'Service Health for MySQL',
                   'width':400,
                   'height':300,
                   'pieHole': 0.4,
                  // colors: ['#e0440e', '#f3b49f', '#f6c7b6'],
                   backgroundColor: { fill:'transparent' }};

    // Instantiate and draw our chart, passing in some options.
    var chart1 = new google.visualization.PieChart(document.getElementById('healthChart2'));
    chart1.draw(data1, options1);
	
}

function drawBar() {
	var data = new google.visualization.DataTable();
    data.addColumn('date', 'Time of Day');
    data.addColumn('number', 'Instance Provisioning');

    data.addRows([
      [new Date(2016, 12, 5), 5],  [new Date(2016, 12, 6), 7],  [new Date(2016, 12, 7), 3],
      [new Date(2016, 12, 8), 1],  [new Date(2016, 12, 9), 3]
    ]);


    var options = {
      title: 'Instance Provision',
      
      height: 500,
      hAxis: {
        format: 'M/d/yy', 
      },
      vAxis: {
        gridlines: {color: 'none'},
        minValue: 0
      }, backgroundColor: { fill:'transparent' }};

    var chart = new google.visualization.LineChart(document.getElementById('timeline'));

    chart.draw(data, options);

    var button = document.getElementById('change');

    button.onclick = function () {

      // If the format option matches, change it to the new option,
      // if not, reset it to the original format.
      options.hAxis.format === 'M/d/yy' ?
      options.hAxis.format = 'MMM dd, yyyy' :
      options.hAxis.format = 'M/d/yy';

      chart.draw(data, options);
    };

    
	     
}
