-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jun 24, 2022 at 01:33 PM
-- Server version: 10.4.24-MariaDB
-- PHP Version: 8.1.6

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `msme_mart`
--

-- --------------------------------------------------------

--
-- Table structure for table `tbl_cart`
--

CREATE TABLE `tbl_cart` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `product_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `tbl_cart`
--

INSERT INTO `tbl_cart` (`id`, `user_id`, `product_id`) VALUES
(17, 1, 11),
(18, 1, 6);

-- --------------------------------------------------------

--
-- Table structure for table `tbl_product`
--

CREATE TABLE `tbl_product` (
  `id` int(11) NOT NULL,
  `product_name` varchar(50) NOT NULL,
  `product_desc` varchar(100) NOT NULL,
  `product_price` int(11) NOT NULL,
  `product_quantity` int(11) NOT NULL,
  `product_image` varchar(10) NOT NULL,
  `product_status` tinyint(4) NOT NULL DEFAULT 1,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `tbl_product`
--

INSERT INTO `tbl_product` (`id`, `product_name`, `product_desc`, `product_price`, `product_quantity`, `product_image`, `product_status`, `created_at`) VALUES
(1, 'Platter and Bowl', '499/ Size-10/8/.5 inch, bowl size-4/3 inch', 499, 10, '1.jpg', 1, '2022-06-22 10:27:49'),
(2, 'Chopping Board', '350/ Size-11.5/10/0.75 inch', 350, 10, '2.jpg', 1, '2022-06-22 10:27:56'),
(3, 'Wall Clock', '770/', 740, 10, '3.jpg', 1, '2022-06-22 10:28:10'),
(4, 'Wall Clock', '650/ Size-14 inch length, 15 inch height', 650, 10, '4.jpg', 1, '2022-06-22 10:28:19'),
(5, 'Coaster', '240/', 240, 10, '5.jpg', 1, '2022-06-22 10:28:36'),
(6, 'Coaster', '240/', 240, 10, '6.jpg', 1, '2022-06-22 10:28:41'),
(7, 'Coaster', '200/', 200, 10, '7.jpg', 1, '2022-06-22 10:28:46'),
(8, 'Gramophone Show Piece Cum Music Player', '1099/ size- 7 inch length, 12 inch height', 1099, 10, '8.jpg', 1, '2022-06-22 10:28:52'),
(9, 'Nut Basket', '575/ 12 inch diameter)', 575, 10, '9.jpg', 1, '2022-06-22 10:28:58'),
(10, 'Nut Basket', '560/ (12 inch diameter)', 560, 10, '10.jpg', 1, '2022-06-22 10:29:03'),
(11, 'Platter and Bowl', '320/ Size-9/5.5/1 inch, bowl 3 inch dia', 320, 10, '11.jpg', 1, '2022-06-22 10:29:09'),
(12, 'Platter and Bowl', '320/Size-9/5.5/1 inch, bowl 3 inch dia', 320, 10, '12.jpg', 1, '2022-06-22 10:29:14'),
(13, 'Platter and Bowl', '499/Size-10/8/.5 inch, bowl size-4/3 inch', 499, 10, '13.jpg', 1, '2022-06-22 10:29:22'),
(14, 'Platter Set', '490/ Size-6/6 inch, 8/78inch, 10/10 inch', 490, 10, '14.jpg', 1, '2022-06-22 10:29:27'),
(15, 'Round Serving Tray', '450/ Size- 14 inch diameter, 2 inch height', 450, 10, '15.jpg', 1, '2022-06-22 10:29:31'),
(16, 'Gold and Silver Serving Tray (5 each)', '360/ Size- 12/8/2 inch', 360, 10, '16.jpg', 1, '2022-06-22 10:29:35'),
(17, 'Wall Clock', '770/ Size-16/16 inch', 770, 10, '17.jpg', 1, '2022-06-22 10:29:40'),
(18, 'Serving Platter', '310/size-33/22cm', 310, 10, '18.jpg', 1, '2022-06-22 10:58:12');

-- --------------------------------------------------------

--
-- Table structure for table `tbl_users`
--

CREATE TABLE `tbl_users` (
  `id` int(11) NOT NULL,
  `first_name` varchar(20) NOT NULL,
  `last_name` varchar(20) NOT NULL,
  `mobile` varchar(12) NOT NULL,
  `password` varchar(100) NOT NULL,
  `district` varchar(20) NOT NULL,
  `address` varchar(150) NOT NULL,
  `role` varchar(20) NOT NULL,
  `status` tinyint(4) NOT NULL DEFAULT 1,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `tbl_users`
--

INSERT INTO `tbl_users` (`id`, `first_name`, `last_name`, `mobile`, `password`, `district`, `address`, `role`, `status`, `created_at`) VALUES
(1, 'Durgesh', 'Chaudhary', '9554817161', '6167b770543fb69c941a01c0545b6725', 'Siddharthnagar', 'Village- Niyanw Post Shohratgarh', 'Admin', 1, '2022-06-06 09:10:55'),
(2, 'Rahul', 'Chaudhary', '7007381844', 'b12a43efea07a05e85e826d5299166fe', 'Siddharthnagar', 'Village- Niyanw Post Shohratgarh, District Siddharthnagr Uttar Pradesg-272205', 'User', 1, '2022-06-22 10:50:55'),
(3, 'Durgesh', 'Chaudhary', '9554817162', 'f4c8730c009c37f291afca98d507f9a2', 'Lucknow', 'D Block Indira Nagar, Polytechnic Chauraha Lucknow-226016', 'Admin', 1, '2022-06-22 10:56:53'),
(4, 'Durgesh', 'Chaudhary', '9554817163', '9c1e714e1ba1e2913f92a8a3c0aa6600', 'Lucknow', 'D Block Indira Nagar, Polytechnic Chauraha Lucknow-226016', 'User', 1, '2022-06-22 10:57:22');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `tbl_cart`
--
ALTER TABLE `tbl_cart`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `tbl_product`
--
ALTER TABLE `tbl_product`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `tbl_users`
--
ALTER TABLE `tbl_users`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `tbl_cart`
--
ALTER TABLE `tbl_cart`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=19;

--
-- AUTO_INCREMENT for table `tbl_product`
--
ALTER TABLE `tbl_product`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=19;

--
-- AUTO_INCREMENT for table `tbl_users`
--
ALTER TABLE `tbl_users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
