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

        final String LINE = "\t+" + line(50, "-") + line(10, "-") + line(10, "-");

        Conexion login = new Conexion();
        Connection con = null;
        PreparedStatement stmt = null;
        

        File fileName = null;
        
        try {
            con = login.conectar();

            File folderName = new File("Categorias");

            createFolder(folderName);
            //CONSULTA DE LOS DATOS INSERTADOS
            stmt = con.prepareStatement("SELECT * FROM Categorias ");

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                //tratamiento de fichero usando la categoria
                String categName = rs.getString(2).replace('/', '_') + ".txt";

                //naming
                System.out.println(categName);
                fileName = new File(folderName, categName);

                //formato para el título
                String leftAlignFormat = leftAlignFormat();

                //extraccion del id de la categoria
                int cat = rs.getInt(1);
                stmt = con.prepareStatement("SELECT p.NomProducto, p.precio, p.Existencias from productos p where Categoria=? ");
                stmt.setInt(1, cat);
                ResultSet rs2 = stmt.executeQuery();
                String textToBeWritten = "";

                //titulo tabla
                textToBeWritten += LINE; //first line
                textToBeWritten += String.format(leftAlignFormat, "          NOMBRE PRODUCTO", "PRECIO", "CANT");
                int contador = 0;
                while (rs2.next()) {
                    textToBeWritten += LINE; //middle lines
                    textToBeWritten += String.format(leftAlignFormat, rs2.getString(1), rs2.getInt(2), rs2.getInt(3));
                    contador++;
                }
                textToBeWritten += LINE; // final line
                textToBeWritten += String.format("%-68s  %-5d| ","\n\t|Total Productos: ",contador);
                textToBeWritten += "\n"+LINE; // final line
                writeFile(fileName, textToBeWritten); //file written

                if (rs2 != null) {
                    rs2.close();
                }
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
