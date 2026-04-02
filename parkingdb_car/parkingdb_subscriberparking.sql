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
-- Table structure for table `subscriberparking`
--

DROP TABLE IF EXISTS `subscriberparking`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `subscriberparking` (
  `subscriberCode` int NOT NULL,
  `parkingCode` int NOT NULL,
  `date` date NOT NULL,
  `time` time NOT NULL,
  `status` enum('ACTIVE','NOT ACTIVE') NOT NULL,
  `numberOfExtends` int NOT NULL DEFAULT '0',
  `receivingCarTime` time DEFAULT NULL,
  `carNumber` varchar(45) DEFAULT NULL,
  `confirmation_code` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`parkingCode`,`subscriberCode`),
  KEY `carNumber_idx` (`carNumber`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `subscriberparking`
--

LOCK TABLES `subscriberparking` WRITE;
/*!40000 ALTER TABLE `subscriberparking` DISABLE KEYS */;
INSERT INTO `subscriberparking` VALUES (123,2,'2025-06-03','18:02:00','NOT ACTIVE',0,'20:02:00','11111112','7557'),(17,3,'2025-06-01','15:00:00','ACTIVE',0,NULL,'11111112','2362'),(17,4,'2025-06-01','15:00:00','NOT ACTIVE',0,'21:00:00','2345678','2536'),(123,7,'2025-06-03','15:00:00','NOT ACTIVE',0,'18:13:00','11111111','2536'),(123,10,'2025-06-15','15:00:00','ACTIVE',3,NULL,'2345678','2536'),(17,12,'2025-06-01','15:00:00','NOT ACTIVE',0,'19:00:00','11111111','2536'),(123,13,'2025-06-03','10:00:00','NOT ACTIVE',2,'22:00:00','11111111','2356'),(17,16,'2025-06-01','15:00:00','ACTIVE',0,NULL,'2345678','2453');
/*!40000 ALTER TABLE `subscriberparking` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-06-15 21:16:27
