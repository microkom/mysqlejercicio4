/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mysqlejercicio4;

import java.sql.*;
import java.io.*;
import static mysqlejercicio4.Methods.*;

/**
 * Ejercicio 4: Crear una aplicación que conecte con la BBDD de Tienda. Debe
 * extraer información relativa a los productos de las diferentes categorías.
 * Debe crearse un directorio con el nombre “Categorias” y dentro de él se debe
 * crear un fichero con el nombre de la categoría(.txt) (cuidado con las ‘/’ que
 * deberán ser sustituidas ya que no se permite ficheros con ese carácter en el
 * nombre) e insertar en él toda el nombre y el precio de los productos que le
 * pertenecen. También habrá que añadir al final de cada fichero la cantidad de
 * productos que hay.
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        int id = 0;
        String nombre = null;
        String descripcion = null;

        Categorias categoria = new Categorias(id, nombre, descripcion);

        Conexion login = new Conexion();
        Connection con = null;
        PreparedStatement stmt = null;

        File filename = null;
        FileWriter fw = null;
        BufferedWriter bw = null;

        try {
            con = login.conectar();

            //CONSULTA DE LOS DATOS INSERTADOS
            stmt = con.prepareStatement("SELECT * FROM Categorias ");

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                //tratamiento de fichero usando la categoria
                String categName = rs.getString(2).replace('/', '_') + ".txt";
                File categFolder = new File("Categorias");

                try {
                    categFolder.mkdir();
                } catch (SecurityException se) {
                    System.out.println(se.getMessage());
                }

                System.out.println(categName);
                filename = new File(categFolder, categName);

                 String leftAlignFormat = "\n\t"
                + "|"
                + " %-" + Integer.toString(50 - 1) + "s"
                + "|"
                + "  %-" + Integer.toString(9 - 1) + "s"
                + "|"      
                + "  %-" + Integer.toString(9 - 1) + "s"
                + "|\n";
                 
                //extraccion del id de la categoria
                int cat = rs.getInt(1);
                stmt = con.prepareStatement("SELECT p.NomProducto, p.precio, p.Existencias from productos p where Categoria=? ");
                stmt.setInt(1, cat);
                ResultSet rs2 = stmt.executeQuery();
                String textToBeWritten = "";
                
                //titulo tabla
                textToBeWritten += line(74, "-");
                textToBeWritten += String.format(leftAlignFormat,"          NOMBRE PRODUCTO","  PRECIO","   CANT");
                
                while (rs2.next()) {
                    textToBeWritten += line(74, "-");
                    textToBeWritten += String.format(leftAlignFormat, rs2.getString(1) , rs2.getInt(2) , rs2.getInt(3) );
                    
                }
                
                textToBeWritten += line(74, "-");
                writeFile(filename, textToBeWritten);

                //line(70, "-");
            }
            if (rs != null) {
                rs.close();
            }

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        } finally {
            try {
                //desconexión de la base de datos
                login.desconectar(con);

            } catch (Exception ex) {
                System.out.println("\tBase de datos aun conectada. " + ex.getMessage());
            }
        }
    }

}
