<?php include_once("include/session.php");?>
<?php include_once("include/db_connect.php");?>
<?php include_once("include/functions.php");?>
<?php $token = $_SESSION["token"]; ?>
<?php  
if($token=isset($_POST["token"])){
  
    $user_id          = test_input($_POST["user_id"]);
    $user_status      = test_input($_POST["user_status"]);

    $update          = update_user($con,$user_id,$user_status);

    if($update !=null){  
      $response =true;
      $message  ='Updated successfully';
      $data     =$user_status;
    } 
    else{ 
      $response =false;
      $message  ='Something went wrong';
      $data     =null;
   }

  $response= array('Response'=>$response,'Message'=>$message,'Data'=>$data);
  echo json_encode($response); 

 
}  
                  
?>