<?php
include "config.php";
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $mobile = $_POST['mobile'];
    $message = $_POST['message'];
    if($mobile=='Shinny Q'){
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
