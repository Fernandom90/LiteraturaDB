package com.alura.Literatura;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.alura.Literatura.DataBaseConnection;
import org.json.JSONArray;
import org.json.JSONObject;

public class LiteraturaApplication {

	private static final String BASE_URL = "https://gutendex.com/books?search=";
	private static final Scanner scanner = new Scanner(System.in);

	public static void main(String[] args) {
		while (true) {
			System.out.println("------ Menú de Opciones ------");
			System.out.println("1. Consultar libro por título");
			System.out.println("2. Consultar libros registrados");
			System.out.println("3. Consultar autores registrados");
			System.out.println("4. Listar libros por idioma");
			System.out.println("5. Salir");
			System.out.print("Seleccione una opción: ");

			int opcion = scanner.nextInt();
			scanner.nextLine(); // Consumir el salto de línea después de nextInt()

			switch (opcion) {
				case 1:
					consultarLibroPorTitulo();
					break;
				case 2:
					consultarLibrosRegistrados();
					break;
				case 3:
					consultarAutoresRegistrados();
					break;
				case 4:
					listarLibrosPorIdioma();
					break;
				case 5:
					System.out.println("Saliendo del programa...");
					return;
				default:
					System.out.println("Opción inválida. Por favor, seleccione una opción del 1 al 5.");
			}
		}
	}

	private static void consultarLibroPorTitulo() {
		try {
			System.out.print("Introduce el título del libro a buscar: ");
			String titulo = scanner.nextLine();

			String encodedTitulo = URLEncoder.encode(titulo, "UTF-8");
			String searchUrl = BASE_URL + encodedTitulo;

			URL url = new URL(searchUrl);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");

			int responseCode = con.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;
				StringBuilder response = new StringBuilder();

				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();

				// Procesar la respuesta JSON
				JSONObject jsonResponse = new JSONObject(response.toString());

				JSONArray books = jsonResponse.getJSONArray("results");
				if (books.length() > 0) {
					// Mostrar detalles del primer libro encontrado
					JSONObject book = books.getJSONObject(0);
					String title = book.getString("title");
					String author = getAuthor(book);
					String language = book.optString("languages", "Desconocido");
					int downloads = book.optInt("download_count", 0);

					System.out.println("-------- LIBRO ENCONTRADO --------");
					System.out.println("Título: " + title);
					System.out.println("Autor: " + author);
					System.out.println("Idioma: " + language);
					System.out.println("Número de descargas: " + downloads);
					System.out.println();

					insertarLibroEnBD(title, author, language, downloads);
				} else {
					System.out.println("No se encontraron libros con el título especificado.");
				}
			} else {
				System.out.println("La consulta a Gutendex no fue exitosa. Código de respuesta: " + responseCode);
			}
			con.disconnect();
		} catch (Exception e) {
			System.err.println("Error al consultar libro por título: " + e.getMessage());
		}
	}

	private static String getAuthor(JSONObject book) {
		JSONArray authors = book.optJSONArray("authors");
		if (authors != null && authors.length() > 0) {
			JSONObject firstAuthor = authors.getJSONObject(0);
			return firstAuthor.optString("name", "Autor desconocido");
		} else {
			return "Autor desconocido";
		}
	}

	private static void consultarLibrosRegistrados() {
		try (Connection connection = DataBaseConnection.getConnection();
			 PreparedStatement statement = connection.prepareStatement("SELECT * FROM test");
			 ResultSet resultSet = statement.executeQuery()) {

			List<String> librosRegistrados = new ArrayList<>();
			while (resultSet.next()) {
				String titulo = resultSet.getString("titulo");
				librosRegistrados.add(titulo);
			}

			if (!librosRegistrados.isEmpty()) {
				System.out.println("------ LIBROS REGISTRADOS ------");
				for (String titulo : librosRegistrados) {
					System.out.println(titulo);
				}
				System.out.println();
			} else {
				System.out.println("No hay libros registrados en la base de datos.");
			}

		} catch (SQLException e) {
			System.err.println("Error al consultar libros registrados: " + e.getMessage());
		}
	}

	private static void consultarAutoresRegistrados() {
		try (Connection connection = DataBaseConnection.getConnection();
			 PreparedStatement statement = connection.prepareStatement("SELECT DISTINCT autor FROM test");
			 ResultSet resultSet = statement.executeQuery()) {

			List<String> autoresRegistrados = new ArrayList<>();
			while (resultSet.next()) {
				String autor = resultSet.getString("autor");
				autoresRegistrados.add(autor);
			}

			if (!autoresRegistrados.isEmpty()) {
				System.out.println("------ AUTORES REGISTRADOS ------");
				for (String autor : autoresRegistrados) {
					System.out.println(autor);
				}
				System.out.println();
			} else {
				System.out.println("No hay autores registrados en la base de datos.");
			}

		} catch (SQLException e) {
			System.err.println("Error al consultar autores registrados: " + e.getMessage());
		}
	}

	private static void listarLibrosPorIdioma() {
		try (Connection connection = DataBaseConnection.getConnection();
			 PreparedStatement statement = connection.prepareStatement("SELECT * FROM test WHERE idioma IS NOT NULL");
			 ResultSet resultSet = statement.executeQuery()) {

			List<String> librosPorIdioma = new ArrayList<>();
			while (resultSet.next()) {
				String titulo = resultSet.getString("titulo");
				String idioma = resultSet.getString("idioma");
				librosPorIdioma.add(titulo + " - " + idioma);
			}

			if (!librosPorIdioma.isEmpty()) {
				System.out.println("------ LIBROS POR IDIOMA ------");
				for (String info : librosPorIdioma) {
					System.out.println(info);
				}
				System.out.println();
			} else {
				System.out.println("No hay libros registrados con información de idioma en la base de datos.");
			}

		} catch (SQLException e) {
			System.err.println("Error al listar libros por idioma: " + e.getMessage());
		}
	}

	private static void insertarLibroEnBD(String titulo, String autor, String idioma, int descargas) {
		try (Connection connection = DataBaseConnection.getConnection();
			 PreparedStatement statement = connection.prepareStatement(
					 "INSERT INTO test (titulo, autor, idioma, descargas) VALUES (?, ?, ?, ?)")) {

			statement.setString(1, titulo);
			statement.setString(2, autor);
			statement.setString(3, idioma);
			statement.setInt(4, descargas);

			int filasInsertadas = statement.executeUpdate();
			if (filasInsertadas > 0) {
				System.out.println("Libro insertado en la base de datos correctamente.");
			} else {
				System.out.println("No se pudo insertar el libro en la base de datos.");
			}

		} catch (SQLException e) {
			System.err.println("Error al insertar libro en la base de datos: " + e.getMessage());
		}
	}
}
