-- MySQL dump 10.13  Distrib 8.0.41, for Win64 (x86_64)
--
-- Host: localhost    Database: parkingdb
-- ------------------------------------------------------
-- Server version	8.0.41

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `order`
--

DROP TABLE IF EXISTS `order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order` (
  `parking_space` int NOT NULL,
  `order_number` int NOT NULL AUTO_INCREMENT,
  `order_date` date NOT NULL,
  `confirmation_code` int NOT NULL,
  `subscriber_id` int NOT NULL,
  `date_of_placing_an_order` date NOT NULL,
  `car_number` varchar(45) DEFAULT NULL,
  `startTime` time DEFAULT NULL,
  `endTime` time DEFAULT NULL,
  PRIMARY KEY (`order_number`),
  KEY `car_number_idx` (`car_number`),
  KEY `subsriber_code_idx` (`subscriber_id`),
  CONSTRAINT `subscriber_code` FOREIGN KEY (`subscriber_id`) REFERENCES `subscribers` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=162 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order`
--

LOCK TABLES `order` WRITE;
/*!40000 ALTER TABLE `order` DISABLE KEYS */;
INSERT INTO `order` VALUES (20,153,'2025-06-01',365,78,'2025-06-20','11111112','14:00:00','18:00:00'),(1,154,'2025-06-01',1,17,'2025-05-30','11111112','14:52:28','18:52:28'),(14,155,'2025-06-01',1,17,'2025-05-30','2345678','14:53:57','18:53:57'),(8,156,'2025-06-01',4098,17,'2025-05-31','11111112','12:00:00','13:00:00'),(5,157,'2025-06-04',5801,17,'2025-05-31','11111112','01:00:00','04:00:00'),(4,158,'2025-06-01',6078,17,'2025-05-31','11111112','00:00:00','02:00:00'),(11,159,'2025-06-01',6299,17,'2025-05-31','11111112','00:00:00','02:00:00'),(6,160,'2025-06-01',8613,17,'2025-05-31','11111112','00:00:00','01:00:00'),(5,161,'2025-06-01',4114,17,'2025-05-31','11111112','00:00:00','01:00:00');
/*!40000 ALTER TABLE `order` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-06-15 21:16:26
