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
-- Table structure for table `latemessage`
--

DROP TABLE IF EXISTS `latemessage`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `latemessage` (
  `number` int NOT NULL AUTO_INCREMENT,
  `text` text NOT NULL,
  `date` date NOT NULL,
  `subscriberCode` int NOT NULL,
  `parkingCode` int NOT NULL,
  `numberOfWarnings` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`number`),
  KEY `parkngSubscriber_idx` (`subscriberCode`,`parkingCode`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `latemessage`
--

LOCK TABLES `latemessage` WRITE;
/*!40000 ALTER TABLE `latemessage` DISABLE KEYS */;
INSERT INTO `latemessage` VALUES (1,'Late for receiving the car','2025-05-30',123,12,41),(2,'Late for receiving the car','2025-05-30',17,12,21),(3,'Late for receiving the car','2025-05-31',123,17,3),(4,'Late for receiving the car','2025-05-31',123,2,22),(5,'Late for receiving the car','2025-05-31',17,3,13),(6,'Late for receiving the car','2025-05-31',17,4,13),(7,'Late for receiving the car','2025-05-31',123,7,13),(8,'Late for receiving the car','2025-05-31',123,9,13),(9,'Late for receiving the car','2025-05-31',123,10,13),(10,'Late for receiving the car','2025-05-31',17,16,13),(11,'Late for receiving the car','2025-06-03',123,13,1);
/*!40000 ALTER TABLE `latemessage` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-06-15 21:16:24
