<!DOCTYPE html>
<%@include file="inc/default-taglibs.jsp" %>
<html>
    <head>
        <%@include file="inc/default-head.jsp" %>
    </head>
    <body>
        <%@include file="inc/header.jsp" %>
        <%@include file="inc/menu/index-menu.jsp" %>
        <div id="container">
            <div id="content">
                <div id="content-header">
                    <h1>${jpm.subtitle}</h1>
                </div>
                <div id="breadcrumb">
                    <a href="#" title="" class="tip-bottom" class="current"><i class="glyphicon glyphicon-home"></i> <spring:message code="jpm.index.home" text="Home" /></a>
                </div>
                <div class="container-fluid">
                    <div class="row">
                        <div class="col-12 center" style="text-align: center;">					
                            <ul class="stat-boxes">
                                <li class="popover-visits" data-original-title="" title="">
                                    <div class="left sparkline_bar_good"><span><canvas width="41" height="24" style="display: inline-block; width: 41px; height: 24px; vertical-align: top;"></canvas></span>+10%</div>
                                    <div class="right">
                                        <strong>36094</strong>
                                        Visits
                                    </div>
                                </li>
                                <li class="popover-users" data-original-title="" title="">
                                    <div class="left sparkline_bar_neutral"><span><canvas width="47" height="24" style="display: inline-block; width: 47px; height: 24px; vertical-align: top;"></canvas></span>0%</div>
                                    <div class="right">
                                        <strong>1433</strong>
                                        Users
                                    </div>
                                </li>
                                <li class="popover-orders" data-original-title="" title="">
                                    <div class="left sparkline_bar_bad"><span><canvas width="41" height="24" style="display: inline-block; width: 41px; height: 24px; vertical-align: top;"></canvas></span>-50%</div>
                                    <div class="right">
                                        <strong>8650</strong>
                                        Orders
                                    </div>
                                </li>
                                <li class="popover-tickets" data-original-title="" title="">
                                    <div class="left sparkline_line_good"><span><canvas width="50" height="24" style="display: inline-block; width: 50px; height: 24px; vertical-align: top;"></canvas></span>+70%</div>
                                    <div class="right">
                                        <strong>2968</strong>
                                        Tickets
                                    </div>
                                </li>
                            </ul>
                        </div>	
                    </div>
                    <div class="row">
                        <div class="col-12">
                            <div class="widget-box">
                                <div class="widget-title"><span class="icon"><i class="glyphicon glyphicon-signal"></i></span><h5>Site Statistics</h5><div class="buttons"><a href="#" class="btn"><i class="glyphicon glyphicon-refresh"></i> Update stats</a></div></div>
                                <div class="widget-content">
                                    <div class="row">
                                        <div class="col-12 col-sm-4">
                                            <ul class="site-stats">
                                                <li><div class="cc"><i class="glyphicon glyphicon-user"></i> <strong>1433</strong> <small>Total Users</small></div></li>
                                                <li><div class="cc"><i class="glyphicon glyphicon-arrow-right"></i> <strong>16</strong> <small>New Users (last week)</small></div></li>
                                                <li class="divider"></li>
                                                <li><div class="cc"><i class="glyphicon glyphicon-shopping-cart"></i> <strong>259</strong> <small>Total Shop Items</small></div></li>
                                                <li><div class="cc"><i class="glyphicon glyphicon-tag"></i> <strong>8650</strong> <small>Total Orders</small></div></li>
                                                <li><div class="cc"><i class="glyphicon glyphicon-repeat"></i> <strong>29</strong> <small>Pending Orders</small></div></li>
                                            </ul>
                                        </div>
                                        <div class="col-12 col-sm-8">
                                            <div class="chart" style="padding: 0px; position: relative;"><canvas class="base" width="1039" height="300"></canvas><canvas class="overlay" width="1039" height="300" style="position: absolute; left: 0px; top: 0px;"></canvas><div class="tickLabels" style="font-size:smaller"><div class="xAxis x1Axis" style="color:#545454"><div class="tickLabel" style="position:absolute;text-align:center;left:-6px;top:283px;width:69px">0</div><div class="tickLabel" style="position:absolute;text-align:center;left:68px;top:283px;width:69px">1</div><div class="tickLabel" style="position:absolute;text-align:center;left:143px;top:283px;width:69px">2</div><div class="tickLabel" style="position:absolute;text-align:center;left:217px;top:283px;width:69px">3</div><div class="tickLabel" style="position:absolute;text-align:center;left:292px;top:283px;width:69px">4</div><div class="tickLabel" style="position:absolute;text-align:center;left:366px;top:283px;width:69px">5</div><div class="tickLabel" style="position:absolute;text-align:center;left:441px;top:283px;width:69px">6</div><div class="tickLabel" style="position:absolute;text-align:center;left:516px;top:283px;width:69px">7</div><div class="tickLabel" style="position:absolute;text-align:center;left:590px;top:283px;width:69px">8</div><div class="tickLabel" style="position:absolute;text-align:center;left:665px;top:283px;width:69px">9</div><div class="tickLabel" style="position:absolute;text-align:center;left:739px;top:283px;width:69px">10</div><div class="tickLabel" style="position:absolute;text-align:center;left:814px;top:283px;width:69px">11</div><div class="tickLabel" style="position:absolute;text-align:center;left:889px;top:283px;width:69px">12</div><div class="tickLabel" style="position:absolute;text-align:center;left:963px;top:283px;width:69px">13</div></div><div class="yAxis y1Axis" style="color:#545454"><div class="tickLabel" style="position:absolute;text-align:right;top:259px;right:1018px;width:21px">-1.5</div><div class="tickLabel" style="position:absolute;text-align:right;top:217px;right:1018px;width:21px">-1.0</div><div class="tickLabel" style="position:absolute;text-align:right;top:174px;right:1018px;width:21px">-0.5</div><div class="tickLabel" style="position:absolute;text-align:right;top:132px;right:1018px;width:21px">0.0</div><div class="tickLabel" style="position:absolute;text-align:right;top:89px;right:1018px;width:21px">0.5</div><div class="tickLabel" style="position:absolute;text-align:right;top:47px;right:1018px;width:21px">1.0</div><div class="tickLabel" style="position:absolute;text-align:right;top:4px;right:1018px;width:21px">1.5</div></div></div><div class="legend"><div style="position: absolute; width: 51px; height: 38px; top: 9px; right: 9px; background-color: rgb(255, 255, 255); opacity: 0.85;"> </div><table style="position:absolute;top:9px;right:9px;;font-size:smaller;color:#545454"><tbody><tr><td class="legendColorBox"><div style="border:1px solid #ccc;padding:1px"><div style="width:4px;height:0;border:5px solid #BA1E20;overflow:hidden"></div></div></td><td class="legendLabel">sin(x)</td></tr><tr><td class="legendColorBox"><div style="border:1px solid #ccc;padding:1px"><div style="width:4px;height:0;border:5px solid #459D1C;overflow:hidden"></div></div></td><td class="legendLabel">cos(x)</td></tr></tbody></table></div></div>
                                        </div>	
                                    </div>							
                                </div>
                            </div>					
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-12 col-sm-6">
                            <div class="widget-box">
                                <div class="widget-title"><span class="icon"><i class="glyphicon glyphicon-file"></i></span><h5>Recent Posts</h5><span title="" class="label label-info tip-left" data-original-title="54 total posts">54</span></div>
                                <div class="widget-content nopadding">
                                    <ul class="recent-posts">
                                        <li>
                                            <div class="user-thumb">
                                                <img width="40" height="40" alt="User" src="img/demo/av2.jpg">
                                            </div>
                                            <div class="article-post">
                                                <span class="user-info"> By: neytiri on 2 Aug 2012, 09:27 AM, IP: 186.56.45.7 </span>
                                                <p>
                                                    <a href="#">Vivamus sed auctor nibh congue, ligula vitae tempus pharetra...</a>
                                                </p>
                                                <a href="#" class="btn btn-primary btn-mini">Edit</a> <a href="#" class="btn btn-success btn-mini">Publish</a> <a href="#" class="btn btn-danger btn-mini">Delete</a>
                                            </div>
                                        </li>
                                        <li>
                                            <div class="user-thumb">
                                                <img width="40" height="40" alt="User" src="img/demo/av3.jpg">
                                            </div>
                                            <div class="article-post">
                                                <span class="user-info"> By: john on on 24 Jun 2012, 04:12 PM, IP: 192.168.24.3 </span>
                                                <p>
                                                    <a href="#">Vivamus sed auctor nibh congue, ligula vitae tempus pharetra...</a>
                                                </p>
                                                <a href="#" class="btn btn-primary btn-mini">Edit</a> <a href="#" class="btn btn-success btn-mini">Publish</a> <a href="#" class="btn btn-danger btn-mini">Delete</a>
                                            </div>
                                        </li>
                                        <li>
                                            <div class="user-thumb">
                                                <img width="40" height="40" alt="User" src="img/demo/av1.jpg">
                                            </div>
                                            <div class="article-post">
                                                <span class="user-info"> By: michelle on 22 Jun 2012, 02:44 PM, IP: 172.10.56.3 </span>
                                                <p>
                                                    <a href="#">Vivamus sed auctor nibh congue, ligula vitae tempus pharetra...</a>
                                                </p>
                                                <a href="#" class="btn btn-primary btn-mini">Edit</a> <a href="#" class="btn btn-success btn-mini">Publish</a> <a href="#" class="btn btn-danger btn-mini">Delete</a>
                                            </div>
                                        </li>
                                        <li class="viewall">
                                            <a title="" class="tip-top" href="#" data-original-title="View all posts"> + View all + </a>
                                        </li>
                                    </ul>
                                </div>
                            </div>
                        </div>
                        <div class="col-12 col-sm-6">
                            <div class="widget-box">
                                <div class="widget-title"><span class="icon"><i class="glyphicon glyphicon-comment"></i></span><h5>Recent Comments</h5><span title="" class="label label-info tip-left" data-original-title="88 total comments">88</span></div>
                                <div class="widget-content nopadding">
                                    <ul class="recent-comments">
                                        <li>
                                            <div class="user-thumb">
                                                <img width="40" height="40" alt="User" src="img/demo/av1.jpg">
                                            </div>
                                            <div class="comments">
                                                <span class="user-info"> User: michelle on IP: 172.10.56.3 </span>
                                                <p>
                                                    <a href="#">Vivamus sed auctor nibh congue, ligula vitae tempus pharetra...</a>
                                                </p>
                                                <a href="#" class="btn btn-primary btn-mini">Edit</a> <a href="#" class="btn btn-success btn-mini">Approve</a> <a href="#" class="btn btn-warning btn-mini">Mark as spam</a> <a href="#" class="btn btn-danger btn-mini">Delete</a>
                                            </div>
                                        </li>
                                        <li>
                                            <div class="user-thumb">
                                                <img width="40" height="40" alt="User" src="img/demo/av3.jpg">
                                            </div>
                                            <div class="comments">
                                                <span class="user-info"> User: john on IP: 192.168.24.3 </span>
                                                <p>
                                                    <a href="#">Vivamus sed auctor nibh congue, ligula vitae tempus pharetra...</a>
                                                </p>
                                                <a href="#" class="btn btn-primary btn-mini">Edit</a> <a href="#" class="btn btn-success btn-mini">Approve</a> <a href="#" class="btn btn-warning btn-mini">Mark as spam</a> <a href="#" class="btn btn-danger btn-mini">Delete</a>
                                            </div>
                                        </li>
                                        <li>
                                            <div class="user-thumb">
                                                <img width="40" height="40" alt="User" src="img/demo/av2.jpg">
                                            </div>
                                            <div class="comments">
                                                <span class="user-info"> User: neytiri on IP: 186.56.45.7 </span>
                                                <p>
                                                    <a href="#">Vivamus sed auctor nibh congue, ligula vitae tempus pharetra...</a>
                                                </p>
                                                <a href="#" class="btn btn-primary btn-mini">Edit</a> <a href="#" class="btn btn-success btn-mini">Approve</a> <a href="#" class="btn btn-warning btn-mini">Mark as spam</a> <a href="#" class="btn btn-danger btn-mini">Delete</a>
                                            </div>
                                        </li>
                                        <li class="viewall">
                                            <a title="" class="tip-top" href="#" data-original-title="View all comments"> + View all + </a>
                                        </li>
                                    </ul>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <%@include  file="inc/footer.jsp" %>
        <%@include  file="inc/default-javascript.jsp" %>
        <script type="text/javascript">
            jpmLoad(function() {
                $("#menu-home").addClass("active");
            });
        </script>
    </body>
</html>