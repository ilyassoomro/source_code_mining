/*
SQLyog Enterprise - MySQL GUI v7.02 
MySQL - 5.1.52-community : Database - ns_cvs
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


/*Table structure for table `cvs_author` */
DROP TABLE IF EXISTS `cvs_author`;

CREATE TABLE `cvs_author` (
  `author_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(200) NOT NULL,
  PRIMARY KEY (`author_id`),
  UNIQUE KEY `unique_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=154 DEFAULT CHARSET=latin1;

/*Table structure for table `cvs_file` */

DROP TABLE IF EXISTS `cvs_file`;

CREATE TABLE `cvs_file` (
  `file_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `path` varchar(200) DEFAULT NULL,
  `file_type_id` int(11) DEFAULT NULL,
  `size` int(11) DEFAULT NULL,
  `project_id` int(11) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `created_by` int(11) DEFAULT NULL,
  `modified_date` datetime DEFAULT NULL,
  `modified_by` int(11) DEFAULT NULL,
  PRIMARY KEY (`file_id`),
  UNIQUE KEY `unique_file` (`name`,`path`,`project_id`)
) ENGINE=InnoDB AUTO_INCREMENT=179 DEFAULT CHARSET=latin1;

/*Table structure for table `cvs_repository` */

DROP TABLE IF EXISTS `cvs_repository`;

CREATE TABLE `cvs_repository` (
  `repository_id` int(11) NOT NULL AUTO_INCREMENT,
  `host` varchar(200) NOT NULL,
  `port` varchar(50) DEFAULT NULL,
  `path` varchar(200) DEFAULT NULL,
  `connection_type` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`repository_id`),
  UNIQUE KEY `unique_repository` (`host`,`port`,`path`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `cvs_tag` */

DROP TABLE IF EXISTS `cvs_tag`;

CREATE TABLE `cvs_tag` (
  `tag_id` int(11) NOT NULL AUTO_INCREMENT,
  `tag_name` varchar(200) NOT NULL,
  `is_base` tinyint(1) DEFAULT NULL,
  `is_head` tinyint(1) DEFAULT NULL,
  `branch_number` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`tag_id`),
  UNIQUE KEY `unique_tag` (`tag_name`)
) ENGINE=InnoDB AUTO_INCREMENT=1388 DEFAULT CHARSET=latin1;

/*Table structure for table `file_revision` */

DROP TABLE IF EXISTS `file_revision`;

CREATE TABLE `file_revision` (
  `file_revision_id` int(11) NOT NULL AUTO_INCREMENT,
  `file_id` int(11) NOT NULL,
  `revision` varchar(200) NOT NULL,
  `author_id` int(11) NOT NULL,
  `committed_date` datetime NOT NULL,
  `comments` text,
  `delta` longblob,
  PRIMARY KEY (`file_revision_id`),
  UNIQUE KEY `unique_file_revision` (`file_id`,`revision`,`committed_date`)
) ENGINE=InnoDB AUTO_INCREMENT=8340 DEFAULT CHARSET=latin1;

/*Table structure for table `file_revision_tag` */

DROP TABLE IF EXISTS `file_revision_tag`;

CREATE TABLE `file_revision_tag` (
  `file_revision_tag_id` int(11) NOT NULL AUTO_INCREMENT,
  `file_revision_id` int(11) NOT NULL,
  `tag_id` int(11) NOT NULL,
  PRIMARY KEY (`file_revision_tag_id`)
) ENGINE=InnoDB AUTO_INCREMENT=158609 DEFAULT CHARSET=latin1;

/*Table structure for table `file_type` */

DROP TABLE IF EXISTS `file_type`;

CREATE TABLE `file_type` (
  `file_type_id` int(11) NOT NULL AUTO_INCREMENT,
  `extension` varchar(20) NOT NULL,
  PRIMARY KEY (`file_type_id`),
  UNIQUE KEY `unique_extension` (`extension`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=latin1;

/*Table structure for table `project` */

DROP TABLE IF EXISTS `project`;

CREATE TABLE `project` (
  `project_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `path` varchar(200) DEFAULT NULL,
  `workspace_id` int(11) DEFAULT NULL,
  `repository_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`project_id`),
  UNIQUE KEY `unique_project` (`name`,`path`,`workspace_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

/*Table structure for table `workspace` */

DROP TABLE IF EXISTS `workspace`;

CREATE TABLE `workspace` (
  `workspace_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) DEFAULT NULL,
  `path` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`workspace_id`),
  UNIQUE KEY `unique_workspace` (`path`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
