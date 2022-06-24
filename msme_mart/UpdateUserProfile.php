<?php include_once("include/session.php");?>
<?php include_once("include/db_connect.php");?>
<?php include_once("include/functions.php");?>
<?php $token = $_SESSION["token"]; ?>
<?php  
if($token=isset($_POST["token"])){
  
    $user_id          = test_input($_POST["user_id"]);
    $first_name       = test_input($_POST["first_name"]);
    $last_name        = test_input($_POST["last_name"]);
    $user_mobile      = test_input($_POST["user_mobile"]);
    $user_new_mobile  = test_input($_POST["user_new_mobile"]);

    if ($user_mobile==$user_new_mobile) {

        $UName=strtoupper(substr($first_name, 0,3));
        $UMobile=strtoupper(substr($user_mobile, 6,9));
        $password=md5($UName.$UMobile);

        $update   = update_user_profile($con,$user_id,$first_name,$last_name,$user_mobile,$password);

        $response =true;
        $message  ='Updated successfully';
        $data     =$user_mobile;
    }
    else{ 

        $checkuser=check_mobile($con,$user_new_mobile);

        $UName=strtoupper(substr($first_name, 0,3));
        $UMobile=strtoupper(substr($user_new_mobile, 6,9));
        $password=md5($UName.$UMobile);

       if($checkuser->rowCount()==0){

          $update          = update_user_profile($con,$user_id,$first_name,$last_name,$user_new_mobile,$password);

          $response =true;
          $message  ='Updated successfully';
          $data     =$user_new_mobile;
    }
    
    else if($checkuser->rowCount()>0){
            $response =false;
            $message  ='Mobile number already registered with us';
            $data     =null;
    }

}
    
  $response= array('Response'=>$response,'Message'=>$message,'Data'=>$data);
  echo json_encode($response); 

 
}  
                  
?>