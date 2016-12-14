-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               10.1.19-MariaDB - mariadb.org binary distribution
-- Server OS:                    Win64
-- HeidiSQL Version:             9.3.0.4984
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

-- Dumping database structure for payment
CREATE DATABASE IF NOT EXISTS `payment` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `payment`;

CREATE USER 'testuser2'@'localhost' IDENTIFIED BY '1234asdf';
GRANT ALL PRIVILEGES ON * . * TO 'testuser2'@'localhost';

-- Dumping structure for table payment.payment_transaction
CREATE TABLE IF NOT EXISTS `payment_transaction` (
  `id` varchar(128) NOT NULL,
  `from_account_number` varchar(64) DEFAULT NULL,
  `amount` decimal(20,6) DEFAULT NULL,
  `store_code` varchar(64) DEFAULT NULL,
  `trx_date_time` timestamp NULL DEFAULT NULL,
  `trx_status` varchar(64) DEFAULT NULL,
  `channel` varchar(64) DEFAULT NULL,
  `created_by` varchar(64) DEFAULT NULL,
  `created_date` timestamp NULL DEFAULT NULL,
  `updated_by` varchar(64) DEFAULT NULL,
  `updated_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
