function balanceQuery() {
    document.getElementById("result").innerHTML = "查询中，请稍等...";
    document.getElementById("msg").innerHTML = "";
    document.getElementById("chart").style.display = "none";
    const apikey = document.getElementById("apikey").value;
    const apiKeyPattern = /^sk-[a-zA-Z0-9]{48}$/; // 正则表达式
    if (!apiKeyPattern.test(apikey)) {
        document.getElementById("result").innerHTML = "apikey输入错误，请重新输入";
        return;
    }
    const xhr = new XMLHttpRequest();
    const url = "/balanceQuery?apikey=" + apikey;
    xhr.open("GET", url);
    xhr.onload = function () {
        if (xhr.status === 200) {
            const result = JSON.parse(xhr.responseText);
            if (result.code === 0) {
                document.getElementById("result").innerHTML = "总额：" + result.data.total + "<br>已使用：" + result.data.used + "<br>余额：" + result.data.balance;
                if (result.data.expired) {
                    document.getElementById("msg").innerHTML = "注：此apikey已过期，无法继续使用。";
                } else {
                    document.getElementById("msg").innerHTML = "注：已付费apikey的总额为每月限额，未付费的为赠送金额。";
                }
                document.getElementById("chart").style.display = "block";
                // 渲染柱状图
                renderBarChart(result.data.dailyCosts);
            } else {
                document.getElementById("result").innerHTML = result.msg;
            }
        } else {
            document.getElementById("result").innerHTML = "请求出错";
        }
    };
    xhr.send();
}

function renderBarChart(dailyCosts) {
    const chartData = [];
    const dates = [];
    const costs = [];
    Object.keys(dailyCosts).forEach(function (date) {
        dates.push(date);
        costs.push(dailyCosts[date]);
    });
    chartData.push(dates);
    chartData.push(costs);
    const chart = echarts.init(document.getElementById("chart"));
    const option = {
        title: {
            text: '每日消费情况',
            left: 'center'
        },
        tooltip: {
            trigger: 'axis'
        },
        xAxis: {
            type: 'category',
            data: chartData[0]
        },
        yAxis: {
            type: 'value'
        },
        series: [{
            data: chartData[1],
            type: 'bar',
            itemStyle: {
                color: '#759aa0',
                opacity: 0.7
            },
            emphasis: {
                itemStyle: {
                    opacity: 1
                }
            },
            label: {
                show: true,
                position: 'top'
            }
        }]
    };
    chart.setOption(option);
}
