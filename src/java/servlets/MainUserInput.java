/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
*/
package servlets;


import beans.GeneGoGoNames;
import beans.UserRequest;
import beans.view.CountGOTermsOverTimeBean;
import database.GotrackDB;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import utils.ViewUtils;
import utils.WebUtils;

/**
*
* @author asedeno
*/
@WebServlet(name = "MainUserInput", urlPatterns = {"/MainUserInput"})
public class MainUserInput extends HttpServlet {
private GotrackDB dao = new GotrackDB();
    /**
* Processes requests for both HTTP
* <code>GET</code> and
* <code>POST</code> methods.
*
* @param request servlet request
* @param response servlet response
* @throws ServletException if a servlet-specific error occurs
* @throws IOException if an I/O error occurs
*/
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String species = request.getParameter("species").toLowerCase();                
                String symbol = request.getParameter("symbol");
         String m = request.getParameter("MF");
         String p = request.getParameter("BP");
         String c = request.getParameter("CC");
            
            /*Build list of unique functions*/
            HashSet<String> allIdsToConsider = ViewUtils.getAllGenesInHistory(species,symbol,dao);
            
            
            ArrayList<CountGOTermsOverTimeBean> count = dao.countDirectInferredParents(species, allIdsToConsider);
            //ArrayList<countViewBean > countView = ViewUtils.getCount(count);
            String dataString = ViewUtils.transformString(count,null);
            HttpSession session = request.getSession();
            
            ArrayList<GeneGoGoNames> resForP = dao.getGeneGotermGoName(allIdsToConsider, p, species);
         ArrayList<GeneGoGoNames> resForM = dao.getGeneGotermGoName(allIdsToConsider, m, species);
            ArrayList<GeneGoGoNames> resForC = dao.getGeneGotermGoName(allIdsToConsider, c, species);
            
            ArrayList<GeneGoGoNames> resForAll = new ArrayList<GeneGoGoNames>();
            resForAll.addAll(resForM);
            resForAll.addAll(resForP);
            resForAll.addAll(resForC);
                        
            UserRequest req = new UserRequest();
            req.setDbResponse(resForAll);
            req.setSpecies(species);
            req.setSymbol(symbol);
            req.setFunctionsPerOnt(WebUtils.getUniqueFunctions(resForAll));
            
            
            session.setAttribute("userRequest", req);
            
         ServletContext sc = getServletContext();
         RequestDispatcher rd = sc.getRequestDispatcher("/index.jsp");

            //request.setAttribute("count", countView );
            request.setAttribute("countData", dataString);
         request.setAttribute("res", req );
         rd.forward(request, response);
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
* Handles the HTTP
* <code>GET</code> method.
*
* @param request servlet request
* @param response servlet response
* @throws ServletException if a servlet-specific error occurs
* @throws IOException if an I/O error occurs
*/
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
* Handles the HTTP
* <code>POST</code> method.
*
* @param request servlet request
* @param response servlet response
* @throws ServletException if a servlet-specific error occurs
* @throws IOException if an I/O error occurs
*/
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
* Returns a short description of the servlet.
*
* @return a String containing servlet description
*/
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
