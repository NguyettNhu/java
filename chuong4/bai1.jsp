<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.Locale" %>
<%@ page import="java.text.DecimalFormat" %>
<%
	request.setCharacterEncoding("UTF-8");
	String equationResult = null;
	String triangleResult = null;
	DecimalFormat numberFormat = new DecimalFormat("0.####", java.text.DecimalFormatSymbols.getInstance(Locale.US));

	if ("quadratic".equals(request.getParameter("action"))) {
		try {
			double a = Double.parseDouble(request.getParameter("a"));
			double b = Double.parseDouble(request.getParameter("b"));
			double c = Double.parseDouble(request.getParameter("c"));

			if (a == 0) {
				if (b == 0) {
					equationResult = c == 0 ? "Phương trình có vô số nghiệm." : "Phương trình vô nghiệm.";
				} else {
					equationResult = "Phương trình có nghiệm x = " + numberFormat.format(-c / b) + ".";
				}
			} else {
				double delta = b * b - 4 * a * c;
				if (delta < 0) {
					equationResult = "Phương trình vô nghiệm thực.";
				} else if (delta == 0) {
					equationResult = "Phương trình có nghiệm kép x = " + numberFormat.format(-b / (2 * a)) + ".";
				} else {
					double x1 = (-b + Math.sqrt(delta)) / (2 * a);
					double x2 = (-b - Math.sqrt(delta)) / (2 * a);
					equationResult = "Phương trình có hai nghiệm: x₁ = " + numberFormat.format(x1)
							+ ", x₂ = " + numberFormat.format(x2) + ".";
				}
			}
		} catch (Exception exception) {
			equationResult = "Vui lòng nhập đúng ba số a, b, c.";
		}
	}

	if ("triangle".equals(request.getParameter("action"))) {
		try {
			double a = Double.parseDouble(request.getParameter("sideA"));
			double b = Double.parseDouble(request.getParameter("sideB"));
			double c = Double.parseDouble(request.getParameter("sideC"));
			boolean isTriangle = a > 0 && b > 0 && c > 0
					&& a + b > c && a + c > b && b + c > a;
			triangleResult = isTriangle
					? "Ba số là độ dài ba cạnh của một tam giác."
					: "Ba số không phải là độ dài ba cạnh của một tam giác.";
		} catch (Exception exception) {
			triangleResult = "Vui lòng nhập đúng ba số nguyên a, b, c.";
		}
	}
%>
<!DOCTYPE html>
<html lang="vi">
<head>
	<meta charset="UTF-8">
	<title>Bài tập JSP</title>
	<style>
		body { font-family: Arial, sans-serif; max-width: 760px; margin: 32px auto; padding: 0 20px; }
		h1 { color: #d86f2d; }
		section { border: 1px solid #ccc; padding: 20px; margin: 20px 0; border-radius: 6px; }
		label { display: inline-block; width: 80px; margin: 6px 0; }
		input { width: 120px; padding: 6px; }
		button { margin-top: 12px; padding: 8px 16px; cursor: pointer; }
		.result { margin-top: 16px; font-weight: bold; color: #176b3a; }
	</style>
</head>
<body>
	<h1>Bài tập JSP</h1>

	<section>
		<h2>1. Giải phương trình bậc hai</h2>
		<form method="post">
			<input type="hidden" name="action" value="quadratic">
			<div><label for="a">a:</label><input id="a" name="a" type="number" step="any" required></div>
			<div><label for="b">b:</label><input id="b" name="b" type="number" step="any" required></div>
			<div><label for="c">c:</label><input id="c" name="c" type="number" step="any" required></div>
			<button type="submit">Giải phương trình</button>
		</form>
		<% if (equationResult != null) { %>
			<p class="result"><%= equationResult %></p>
		<% } %>
	</section>

	<section>
		<h2>2. Kiểm tra ba cạnh của tam giác</h2>
		<form method="post">
			<input type="hidden" name="action" value="triangle">
			<div><label for="sideA">a:</label><input id="sideA" name="sideA" type="number" step="any" required></div>
			<div><label for="sideB">b:</label><input id="sideB" name="sideB" type="number" step="any" required></div>
			<div><label for="sideC">c:</label><input id="sideC" name="sideC" type="number" step="any" required></div>
			<button type="submit">Kiểm tra</button>
		</form>
		<% if (triangleResult != null) { %>
			<p class="result"><%= triangleResult %></p>
		<% } %>
	</section>
</body>
</html>
