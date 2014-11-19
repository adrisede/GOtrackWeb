<%@page import="beans.view.countViewBean"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="beans.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        
          <script src='https://rawgithub.com/Polychart/polychart2/develop/polychart2.standalone.js' type='text/javascript'></script>
    
    <style>
    .rChart {
      display: block;
      margin-left: auto;
      margin-right: auto;
      width: 1000px;
      height: 140px;
    }
    </style>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Insert title here</title>
        <!-- Prevents caching at the Proxy Server -->
        

        <meta http-equiv="Expires" content="0" />

        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />

        <meta name="description"
              content="GOtrack is an integral tool that reflects the problems and trends in the stability and annotation biases of multiple model organisms." />

        <meta name="keywords"
              content="genomics,bioinformatics,genetics,gene,function,ontology,biotechnology,medicine,biomedical,meta-analysis,statistics,search,open source,database,software" />

        <meta name="author" content="GOtrack admin (gotrack@chibi.ubc.ca)" />

        <title>UBC-CHiBi|GOtrack</title>
        <link rel="stylesheet" href="css/styles.css" type="text/css" />
    </head>
    <body>
        <div id="wrapper">
            <header>
                <div class="Logo"></div>
            </header>

            <nav>
                <ul>
                    <li class="last"><a href="#" title="Contact"><span>Contact
                                us</span></a></li>
                    <li><a href="#" title="About"><span>About</span></a></li>
                    <li><a href="#" title="Resources"><span>Resources</span></a></li>
                    <li class="active"><a href="#" title="Home"><span>Home</span></a></li>
                </ul>
            </nav>

            <div class="container clearfix">
                <section id="mainContainer" class="left"> <article>
                        <hgroup>
                            <p>GOtrack is an integral tool that reflects the problems and
                                trends in the stability and annotation biases of multiple model
                                organisms.</p>


                            <form action="MainUserInput" method="GET">

                                <p>
                                    Provide a gene that you would like to analyze: <input type="text"
                                                                                          id="symbol" name ="symbol" size="20">
                                </p>

                                From this organism: <select id="species" name="species">                                        
                                    <option>Arabidopsis</option>
                                    <option>Chicken</option>
                                    <option>Cow</option>
                                    <option value="dicty">Dictyostelium</option>
                                    <option>Dog</option>
                                    <option>Ecoli</option>
                                    <option>Fly</option>
                                    <option>Human</option>
                                    <option>Mouse</option>
                                    <option>Pig</option>
                                    <option>Rat</option>
                                    <option>Yeast</option>
                                    <option>Worm</option>
                                    <option>Zebrafish</option>
                                </select>



                                <p>Select Gene Ontology (GO) aspect:</p>

                                <input type="checkbox" name="MF" value="F">Molecular
                                Function<br> <input type="checkbox" name="BP" value="P">Biological
                                Process<br> <input type="checkbox" name="CC" value="C">Cellular
                                Component <br>

                                <input type="submit" value="Submit"><br>
                            </form >
<%
                            String allids = (String) request.getAttribute("countData");
                            if(allids!=null && allids.compareTo("")!=0)
                            {%>
                               <script type="text/javascript" src="//www.google.com/jsapi"></script>
                               <script type="text/javascript">
                                   google.load('visualization', '1', {
                 packages : [ 'motionchart' ]
         });
                                   function drawVisualization() {
                                       <%=allids%>
                                      var motionchart = new google.visualization.MotionChart(document
                                 .getElementById('visualization'));
                 motionchart.draw(data, {
                         'width' : 800,
                         'height' : 400
                 });
                                   }
                                   google.setOnLoadCallback(drawVisualization);
                               </script>
                               <div id="visualization" style="width: 800px; height: 400px;"></div>
                          <%}%>
                            
                            
                            <%ArrayList<countViewBean> count = (ArrayList<countViewBean>) request.getAttribute("count");
                                if (count != null)
                               {%>
                                
                                
                             <%}%>


                            <form action="ProcessGOFunctions" method="GET">
                                <%
                                    UserRequest c = (UserRequest) request.getAttribute("res");
                                    if (c != null) {
                                        if (!c.getFunctionsPerOnt().isEmpty() && !c.isEmpty()) {
                                %> <br><label> Associated functions </label> <br><%

                                                                         for (FunctionsPerOntology dat : c.getFunctionsPerOnt()) {

                                                                             if (!dat.getFunctions().isEmpty() && dat.getFunctions().size() > 0) {

                                                                                 if (dat.getOntology().compareTo("P") == 0) {
                                %>
                                <br><label>Biological Process</label><br>
                                <%}%>
                                <%if (dat.getOntology().compareTo("C") == 0) {%>
                                <br><label>Cellular Component </label><br>
                                <%}%>

                                <%if (dat.getOntology().compareTo("F") == 0) {%>
                                <br><label>Molecular Function</label><br>
                                <%}%>

                                <% for (String function : dat.getFunctions()) {%>
                                <input type="checkbox" name="SelectedGeneFunctions" value="<%=function%>"><%=function%><br>
                                <% }
        }
    }%>
                                <br> <input type="submit" value="Submit"><br>
                                <% } else {%>
                                <br><label>No data</label><br>
                                <% }%>
                                <% }%>
                            </form>
                        </hgroup>
                    </article>
                </section>


                <aside id="rightContainer" class="right">
                    <table>
                        <tr>
                            <th>Top 10 volatile genes (for species x)</th>
                        </tr>
                        <tr>
                            <td>BMP4</td>
                        </tr>
                        <tr>
                            <td>P53</td>
                        </tr>
                        <tr>
                            <th>Top 10 most multifunctional genes (for species x)</th>
                        </tr>
                        <tr>
                            <td>BMP4</td>
                        </tr>
                        <tr>
                            <td>P53</td>
                        </tr>
                        <tr>
                            <th>Top 10 volatile genes (for species x)</th>
                        </tr>
                        <tr>
                            <td>BMP4</td>
                        </tr>
                        <tr>
                            <td>P53</td>
                        </tr>

                    </table>
                </aside>
            </div>

            <footer id="Footer">
                <p>&copy; UBC, Centre of High-throughput Biology, 2014</p>
                <a href="terms.xhtml">Terms and Conditions</a> </footer>
        </div>
    </body>
</html>
