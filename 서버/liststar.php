<?
	$dbc = mysqli_connect("localhost", "root", "apmsetup", "stardb") 
			or die('Error: Connect to Server');

	mysqli_query($dbc, "set names utf8;");

	$query = "SELECT * FROM star";
	$data = mysqli_query($dbc, $query);
	$json = array();

	if (mysqli_num_rows($data)) {
		while ($row = mysqli_fetch_assoc($data)) {
			$json['star'][] = $row;
		}
	}

	mysqli_free_result($data);
	mysqli_close($dbc);

	echo json_encode($json);
?>

