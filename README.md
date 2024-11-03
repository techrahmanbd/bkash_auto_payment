# bkash_auto_payment



---
> getmessage.php

```php
<?php
include "config.php";
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $mobile = $_POST['mobile'];
    $message = $_POST['message'];
    if($mobile=='STARDUST'){
        preg_match('/Tk ([\d,]+\.\d{2})/', $message, $amount_match);
        $amount = $amount_match[1] ?? null;
        preg_match('/from (\d{11})/', $message, $number_match);
        $from_number = $number_match[1] ?? null;
        preg_match('/TrxID (\w+)/', $message, $trx_match);
        $trx_id = $trx_match[1] ?? null;
        
        preg_match('/at (\d{2}\/\d{2}\/\d{4} \d{2}:\d{2})/', $message, $time_match);
        $time = $time_match[1] ?? null;
        
        if (strpos($message, 'You have received') !== false) {
        $type = 'Send Money';
        } elseif (strpos($message, 'Cash In') !== false) {
            $type = 'Cash In';
        } else {
            $type = null;
        }
        if ($amount && $from_number && $trx_id) {
            $stmt = $con->prepare("INSERT INTO data (method,type, number, amount, trx,time) VALUES (?,?,?, ?, ?, ?)");
            $method = 'Cash In';
            $stmt->bind_param("ssssss", $mobile,$type, $from_number, $amount, $trx_id,$time);
            if ($stmt->execute()) {
                echo "Data inserted successfully";
            } else {
                echo "Error: " . $stmt->error;
            }
            $stmt->close();
        }
    }
}
$con->close();
?>

```







---
> verify.php

```php
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

```













