CREATE TABLE `PAYMENTS`.`TRANSACTIONS` (
  `TransactionId` VARCHAR(512) NOT NULL,
  `Amount` DOUBLE NULL,
  `Currency` VARCHAR(200) NULL,
  `PaymentType` VARCHAR(45) NULL,
  `Status` VARCHAR(45) NULL,
  `TransactionTime` VARCHAR(245) NULL,
  PRIMARY KEY (`TransactionId`));