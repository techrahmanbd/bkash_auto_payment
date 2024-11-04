<?php
include "config.php";
if ($_SERVER['REQUEST_METHOD'] == 'GET') {
    if (isset($_GET['trxid'])) {
        $trxid = $_GET['trxid'];
        $stmt = $con->prepare("SELECT * FROM data WHERE trx = ?");
        $stmt->bind_param("s", $trxid);
        $stmt->execute();
        $result = $stmt->get_result();

        if ($result->num_rows > 0) {
            $row = $result->fetch_assoc();
            $method = $row['method'];
            $number = $row['number'];
            $amount = $row['amount'];
            $time = $row['time']; 
            $formatted_time = DateTime::createFromFormat('d/m/Y H:i', $time);

            if ($formatted_time) {
                $date1 = $formatted_time;
                $date2 = new DateTime(); // Current date
                $diff = $date1->diff($date2);

                $response = [
                    "status" => "paid",
                    "method" => $method,
                    "number" => $number,
                    "amount" => $amount,
                    "trxid" => $trxid,
                    "date" => $time,
                    "paid" => $diff->days . " days ago",
                    "paid" => $diff->days . " days ago",
                ];
                header("Content-Type: application/json; charset=UTF-8");
                echo json_encode($response);
            } else {
                echo json_encode(["status" => "unpaid"]);
            }
        } else {
            echo json_encode(["status" => "unpaid"]);
        }

        $stmt->close();
    } else {
        echo json_encode(["error" => "Error: trxid parameter is missing."]);
    }
} else {
    echo json_encode(["error" => "Invalid Request"]);
}

$con->close();
?>
