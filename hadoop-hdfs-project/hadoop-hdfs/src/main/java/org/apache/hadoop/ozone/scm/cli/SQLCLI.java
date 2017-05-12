begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.scm.cli
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|scm
operator|.
name|cli
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|cli
operator|.
name|BasicParser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|cli
operator|.
name|CommandLine
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|cli
operator|.
name|Option
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|cli
operator|.
name|OptionBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|cli
operator|.
name|Options
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|cli
operator|.
name|ParseException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
operator|.
name|Configured
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSUtilClient
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|protocol
operator|.
name|DatanodeID
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|protocol
operator|.
name|proto
operator|.
name|OzoneProtos
operator|.
name|Pipeline
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HdfsProtos
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
operator|.
name|Tool
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
operator|.
name|ToolRunner
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|utils
operator|.
name|LevelDBStore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|iq80
operator|.
name|leveldb
operator|.
name|DBIterator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|Charset
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Paths
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Connection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|DriverManager
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|SQLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Statement
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|OzoneConsts
operator|.
name|BLOCK_DB
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|OzoneConsts
operator|.
name|CONTAINER_DB
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|OzoneConsts
operator|.
name|NODEPOOL_DB
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|OzoneConsts
operator|.
name|OPEN_CONTAINERS_DB
import|;
end_import

begin_comment
comment|/**  * This is the CLI that can be use to convert a levelDB into a sqlite DB file.  *  * NOTE: user should use this CLI in an offline fashion. Namely, this should not  * be used to convert a levelDB that is currently being used by Ozone. Instead,  * this should be used to debug and diagnosis closed levelDB instances.  *  */
end_comment

begin_class
DECL|class|SQLCLI
specifier|public
class|class
name|SQLCLI
extends|extends
name|Configured
implements|implements
name|Tool
block|{
DECL|field|options
specifier|private
name|Options
name|options
decl_stmt|;
DECL|field|parser
specifier|private
name|BasicParser
name|parser
decl_stmt|;
DECL|field|encoding
specifier|private
specifier|final
name|Charset
name|encoding
init|=
name|Charset
operator|.
name|forName
argument_list|(
literal|"UTF-8"
argument_list|)
decl_stmt|;
comment|// for container.db
DECL|field|CREATE_CONTAINER_INFO
specifier|private
specifier|static
specifier|final
name|String
name|CREATE_CONTAINER_INFO
init|=
literal|"CREATE TABLE containerInfo ("
operator|+
literal|"containerName TEXT PRIMARY KEY NOT NULL, "
operator|+
literal|"leaderUUID TEXT NOT NULL)"
decl_stmt|;
DECL|field|CREATE_CONTAINER_MEMBERS
specifier|private
specifier|static
specifier|final
name|String
name|CREATE_CONTAINER_MEMBERS
init|=
literal|"CREATE TABLE containerMembers ("
operator|+
literal|"containerName TEXT NOT NULL, "
operator|+
literal|"datanodeUUID TEXT NOT NULL,"
operator|+
literal|"PRIMARY KEY(containerName, datanodeUUID));"
decl_stmt|;
DECL|field|CREATE_DATANODE_INFO
specifier|private
specifier|static
specifier|final
name|String
name|CREATE_DATANODE_INFO
init|=
literal|"CREATE TABLE datanodeInfo ("
operator|+
literal|"hostName TEXT NOT NULL, "
operator|+
literal|"datanodeUUId TEXT PRIMARY KEY NOT NULL,"
operator|+
literal|"ipAddr TEXT, "
operator|+
literal|"xferPort INTEGER,"
operator|+
literal|"infoPort INTEGER,"
operator|+
literal|"ipcPort INTEGER,"
operator|+
literal|"infoSecurePort INTEGER,"
operator|+
literal|"containerPort INTEGER NOT NULL);"
decl_stmt|;
DECL|field|INSERT_CONTAINER_INFO
specifier|private
specifier|static
specifier|final
name|String
name|INSERT_CONTAINER_INFO
init|=
literal|"INSERT INTO containerInfo (containerName, leaderUUID) "
operator|+
literal|"VALUES (\"%s\", \"%s\")"
decl_stmt|;
DECL|field|INSERT_DATANODE_INFO
specifier|private
specifier|static
specifier|final
name|String
name|INSERT_DATANODE_INFO
init|=
literal|"INSERT INTO datanodeInfo (hostname, datanodeUUid, ipAddr, xferPort, "
operator|+
literal|"infoPort, ipcPort, infoSecurePort, containerPort) "
operator|+
literal|"VALUES (\"%s\", \"%s\", \"%s\", %d, %d, %d, %d, %d)"
decl_stmt|;
DECL|field|INSERT_CONTAINER_MEMBERS
specifier|private
specifier|static
specifier|final
name|String
name|INSERT_CONTAINER_MEMBERS
init|=
literal|"INSERT INTO containerMembers (containerName, datanodeUUID) "
operator|+
literal|"VALUES (\"%s\", \"%s\")"
decl_stmt|;
comment|// for block.db
DECL|field|CREATE_BLOCK_CONTAINER
specifier|private
specifier|static
specifier|final
name|String
name|CREATE_BLOCK_CONTAINER
init|=
literal|"CREATE TABLE blockContainer ("
operator|+
literal|"blockKey TEXT PRIMARY KEY NOT NULL, "
operator|+
literal|"containerName TEXT NOT NULL)"
decl_stmt|;
DECL|field|INSERT_BLOCK_CONTAINER
specifier|private
specifier|static
specifier|final
name|String
name|INSERT_BLOCK_CONTAINER
init|=
literal|"INSERT INTO blockContainer (blockKey, containerName) "
operator|+
literal|"VALUES (\"%s\", \"%s\")"
decl_stmt|;
comment|// for nodepool.db
DECL|field|CREATE_NODE_POOL
specifier|private
specifier|static
specifier|final
name|String
name|CREATE_NODE_POOL
init|=
literal|"CREATE TABLE nodePool ("
operator|+
literal|"datanodeUUID TEXT NOT NULL,"
operator|+
literal|"poolName TEXT NOT NULL,"
operator|+
literal|"PRIMARY KEY(datanodeUUID, poolName))"
decl_stmt|;
DECL|field|INSERT_NODE_POOL
specifier|private
specifier|static
specifier|final
name|String
name|INSERT_NODE_POOL
init|=
literal|"INSERT INTO nodePool (datanodeUUID, poolName) "
operator|+
literal|"VALUES (\"%s\", \"%s\")"
decl_stmt|;
comment|// and reuse CREATE_DATANODE_INFO and INSERT_DATANODE_INFO
comment|// for openContainer.db
DECL|field|CREATE_OPEN_CONTAINER
specifier|private
specifier|static
specifier|final
name|String
name|CREATE_OPEN_CONTAINER
init|=
literal|"CREATE TABLE openContainer ("
operator|+
literal|"containerName TEXT PRIMARY KEY NOT NULL, "
operator|+
literal|"containerUsed INTEGER NOT NULL)"
decl_stmt|;
DECL|field|INSERT_OPEN_CONTAINER
specifier|private
specifier|static
specifier|final
name|String
name|INSERT_OPEN_CONTAINER
init|=
literal|"INSERT INTO openContainer (containerName, containerUsed) "
operator|+
literal|"VALUES (\"%s\", \"%s\")"
decl_stmt|;
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SQLCLI
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|SQLCLI ()
specifier|public
name|SQLCLI
parameter_list|()
block|{
name|this
operator|.
name|options
operator|=
name|getOptions
argument_list|()
expr_stmt|;
name|this
operator|.
name|parser
operator|=
operator|new
name|BasicParser
argument_list|()
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"static-access"
argument_list|)
DECL|method|getOptions ()
specifier|private
name|Options
name|getOptions
parameter_list|()
block|{
name|Options
name|allOptions
init|=
operator|new
name|Options
argument_list|()
decl_stmt|;
name|Option
name|dbPathOption
init|=
name|OptionBuilder
operator|.
name|withArgName
argument_list|(
literal|"levelDB path"
argument_list|)
operator|.
name|withLongOpt
argument_list|(
literal|"dbPath"
argument_list|)
operator|.
name|hasArgs
argument_list|(
literal|1
argument_list|)
operator|.
name|withDescription
argument_list|(
literal|"specify levelDB path"
argument_list|)
operator|.
name|create
argument_list|(
literal|"p"
argument_list|)
decl_stmt|;
name|allOptions
operator|.
name|addOption
argument_list|(
name|dbPathOption
argument_list|)
expr_stmt|;
name|Option
name|outPathOption
init|=
name|OptionBuilder
operator|.
name|withArgName
argument_list|(
literal|"output path"
argument_list|)
operator|.
name|withLongOpt
argument_list|(
literal|"outPath"
argument_list|)
operator|.
name|hasArgs
argument_list|(
literal|1
argument_list|)
operator|.
name|withDescription
argument_list|(
literal|"specify output path"
argument_list|)
operator|.
name|create
argument_list|(
literal|"o"
argument_list|)
decl_stmt|;
name|allOptions
operator|.
name|addOption
argument_list|(
name|outPathOption
argument_list|)
expr_stmt|;
return|return
name|allOptions
return|;
block|}
annotation|@
name|Override
DECL|method|run (String[] args)
specifier|public
name|int
name|run
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|CommandLine
name|commandLine
init|=
name|parseArgs
argument_list|(
name|args
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|commandLine
operator|.
name|hasOption
argument_list|(
literal|"p"
argument_list|)
operator|||
operator|!
name|commandLine
operator|.
name|hasOption
argument_list|(
literal|"o"
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Require dbPath option(-p) AND outPath option (-o)"
argument_list|)
expr_stmt|;
return|return
operator|-
literal|1
return|;
block|}
name|String
name|value
init|=
name|commandLine
operator|.
name|getOptionValue
argument_list|(
literal|"p"
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"levelDB path {}"
argument_list|,
name|value
argument_list|)
expr_stmt|;
comment|// the value is supposed to be an absolute path to a container file
name|Path
name|dbPath
init|=
name|Paths
operator|.
name|get
argument_list|(
name|value
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|Files
operator|.
name|exists
argument_list|(
name|dbPath
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"DB path not exist:{}"
argument_list|,
name|dbPath
argument_list|)
expr_stmt|;
block|}
name|Path
name|parentPath
init|=
name|dbPath
operator|.
name|getParent
argument_list|()
decl_stmt|;
name|Path
name|dbName
init|=
name|dbPath
operator|.
name|getFileName
argument_list|()
decl_stmt|;
if|if
condition|(
name|parentPath
operator|==
literal|null
operator|||
name|dbName
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error processing db path {}"
argument_list|,
name|dbPath
argument_list|)
expr_stmt|;
return|return
operator|-
literal|1
return|;
block|}
name|value
operator|=
name|commandLine
operator|.
name|getOptionValue
argument_list|(
literal|"o"
argument_list|)
expr_stmt|;
name|Path
name|outPath
init|=
name|Paths
operator|.
name|get
argument_list|(
name|value
argument_list|)
decl_stmt|;
if|if
condition|(
name|outPath
operator|==
literal|null
operator|||
name|outPath
operator|.
name|getParent
argument_list|()
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error processing output path {}"
argument_list|,
name|outPath
argument_list|)
expr_stmt|;
return|return
operator|-
literal|1
return|;
block|}
name|Path
name|outParentPath
init|=
name|outPath
operator|.
name|getParent
argument_list|()
decl_stmt|;
if|if
condition|(
name|outParentPath
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|Files
operator|.
name|exists
argument_list|(
name|outParentPath
argument_list|)
condition|)
block|{
name|Files
operator|.
name|createDirectories
argument_list|(
name|outParentPath
argument_list|)
expr_stmt|;
block|}
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Parent path [{}] db name [{}]"
argument_list|,
name|parentPath
argument_list|,
name|dbName
argument_list|)
expr_stmt|;
if|if
condition|(
name|dbName
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
name|CONTAINER_DB
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Converting container DB"
argument_list|)
expr_stmt|;
name|convertContainerDB
argument_list|(
name|dbPath
argument_list|,
name|outPath
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|dbName
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
name|BLOCK_DB
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Converting block DB"
argument_list|)
expr_stmt|;
name|convertBlockDB
argument_list|(
name|dbPath
argument_list|,
name|outPath
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|dbName
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
name|NODEPOOL_DB
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Converting node pool DB"
argument_list|)
expr_stmt|;
name|convertNodePoolDB
argument_list|(
name|dbPath
argument_list|,
name|outPath
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|dbName
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
name|OPEN_CONTAINERS_DB
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Converting open container DB"
argument_list|)
expr_stmt|;
name|convertOpenContainerDB
argument_list|(
name|dbPath
argument_list|,
name|outPath
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unrecognized db name {}"
argument_list|,
name|dbName
argument_list|)
expr_stmt|;
block|}
return|return
literal|0
return|;
block|}
DECL|method|connectDB (String dbPath)
specifier|private
name|Connection
name|connectDB
parameter_list|(
name|String
name|dbPath
parameter_list|)
throws|throws
name|Exception
block|{
name|Class
operator|.
name|forName
argument_list|(
literal|"org.sqlite.JDBC"
argument_list|)
expr_stmt|;
name|String
name|connectPath
init|=
name|String
operator|.
name|format
argument_list|(
literal|"jdbc:sqlite:%s"
argument_list|,
name|dbPath
argument_list|)
decl_stmt|;
return|return
name|DriverManager
operator|.
name|getConnection
argument_list|(
name|connectPath
argument_list|)
return|;
block|}
DECL|method|executeSQL (Connection conn, String sql)
specifier|private
name|void
name|executeSQL
parameter_list|(
name|Connection
name|conn
parameter_list|,
name|String
name|sql
parameter_list|)
throws|throws
name|SQLException
block|{
try|try
init|(
name|Statement
name|stmt
init|=
name|conn
operator|.
name|createStatement
argument_list|()
init|)
block|{
name|stmt
operator|.
name|executeUpdate
argument_list|(
name|sql
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Convert container.db to sqlite. The schema of sql db:    * three tables, containerId, containerMachines, datanodeInfo    * (* for primary key)    *    * containerInfo:    * ----------------------------------------------    * container name* | container lead datanode uuid    * ----------------------------------------------    *    * containerMembers:    * --------------------------------    * container name* |  datanodeUUid*    * --------------------------------    *    * datanodeInfo:    * ---------------------------------------------------------    * hostname | datanodeUUid* | xferPort | infoPort | ipcPort    * ---------------------------------------------------------    *    * --------------------------------    * | infoSecurePort | containerPort    * --------------------------------    *    * @param dbPath path to container db.    * @param outPath path to output sqlite    * @throws IOException throws exception.    */
DECL|method|convertContainerDB (Path dbPath, Path outPath)
specifier|private
name|void
name|convertContainerDB
parameter_list|(
name|Path
name|dbPath
parameter_list|,
name|Path
name|outPath
parameter_list|)
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Create tables for sql container db."
argument_list|)
expr_stmt|;
name|File
name|dbFile
init|=
name|dbPath
operator|.
name|toFile
argument_list|()
decl_stmt|;
name|org
operator|.
name|iq80
operator|.
name|leveldb
operator|.
name|Options
name|dbOptions
init|=
operator|new
name|org
operator|.
name|iq80
operator|.
name|leveldb
operator|.
name|Options
argument_list|()
decl_stmt|;
try|try
init|(
name|LevelDBStore
name|dbStore
init|=
operator|new
name|LevelDBStore
argument_list|(
name|dbFile
argument_list|,
name|dbOptions
argument_list|)
init|;
name|Connection
name|conn
operator|=
name|connectDB
argument_list|(
name|outPath
operator|.
name|toString
argument_list|()
argument_list|)
init|;
name|DBIterator
name|iter
operator|=
name|dbStore
operator|.
name|getIterator
argument_list|()
init|)
block|{
name|executeSQL
argument_list|(
name|conn
argument_list|,
name|CREATE_CONTAINER_INFO
argument_list|)
expr_stmt|;
name|executeSQL
argument_list|(
name|conn
argument_list|,
name|CREATE_CONTAINER_MEMBERS
argument_list|)
expr_stmt|;
name|executeSQL
argument_list|(
name|conn
argument_list|,
name|CREATE_DATANODE_INFO
argument_list|)
expr_stmt|;
name|iter
operator|.
name|seekToFirst
argument_list|()
expr_stmt|;
name|HashSet
argument_list|<
name|String
argument_list|>
name|uuidChecked
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|byte
index|[]
argument_list|,
name|byte
index|[]
argument_list|>
name|entry
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|containerName
init|=
operator|new
name|String
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|encoding
argument_list|)
decl_stmt|;
name|Pipeline
name|pipeline
init|=
name|Pipeline
operator|.
name|parseFrom
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
decl_stmt|;
name|insertContainerDB
argument_list|(
name|conn
argument_list|,
name|containerName
argument_list|,
name|pipeline
argument_list|,
name|uuidChecked
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Insert into the sqlite DB of container.db.    * @param conn the connection to the sqlite DB.    * @param containerName the name of the container.    * @param pipeline the actual container pipeline object.    * @param uuidChecked the uuid that has been already inserted.    * @throws SQLException throws exception.    */
DECL|method|insertContainerDB (Connection conn, String containerName, Pipeline pipeline, Set<String> uuidChecked)
specifier|private
name|void
name|insertContainerDB
parameter_list|(
name|Connection
name|conn
parameter_list|,
name|String
name|containerName
parameter_list|,
name|Pipeline
name|pipeline
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|uuidChecked
parameter_list|)
throws|throws
name|SQLException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Insert to sql container db, for container {}"
argument_list|,
name|containerName
argument_list|)
expr_stmt|;
name|String
name|insertContainerInfo
init|=
name|String
operator|.
name|format
argument_list|(
name|INSERT_CONTAINER_INFO
argument_list|,
name|containerName
argument_list|,
name|pipeline
operator|.
name|getLeaderID
argument_list|()
argument_list|)
decl_stmt|;
name|executeSQL
argument_list|(
name|conn
argument_list|,
name|insertContainerInfo
argument_list|)
expr_stmt|;
for|for
control|(
name|HdfsProtos
operator|.
name|DatanodeIDProto
name|dnID
range|:
name|pipeline
operator|.
name|getMembersList
argument_list|()
control|)
block|{
name|String
name|uuid
init|=
name|dnID
operator|.
name|getDatanodeUuid
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|uuidChecked
operator|.
name|contains
argument_list|(
name|uuid
argument_list|)
condition|)
block|{
comment|// we may also not use this checked set, but catch exception instead
comment|// but this seems a bit cleaner.
name|String
name|ipAddr
init|=
name|dnID
operator|.
name|getIpAddr
argument_list|()
decl_stmt|;
name|String
name|hostName
init|=
name|dnID
operator|.
name|getHostName
argument_list|()
decl_stmt|;
name|int
name|xferPort
init|=
name|dnID
operator|.
name|hasXferPort
argument_list|()
condition|?
name|dnID
operator|.
name|getXferPort
argument_list|()
else|:
literal|0
decl_stmt|;
name|int
name|infoPort
init|=
name|dnID
operator|.
name|hasInfoPort
argument_list|()
condition|?
name|dnID
operator|.
name|getInfoPort
argument_list|()
else|:
literal|0
decl_stmt|;
name|int
name|securePort
init|=
name|dnID
operator|.
name|hasInfoSecurePort
argument_list|()
condition|?
name|dnID
operator|.
name|getInfoSecurePort
argument_list|()
else|:
literal|0
decl_stmt|;
name|int
name|ipcPort
init|=
name|dnID
operator|.
name|hasIpcPort
argument_list|()
condition|?
name|dnID
operator|.
name|getIpcPort
argument_list|()
else|:
literal|0
decl_stmt|;
name|int
name|containerPort
init|=
name|dnID
operator|.
name|getContainerPort
argument_list|()
decl_stmt|;
name|String
name|insertMachineInfo
init|=
name|String
operator|.
name|format
argument_list|(
name|INSERT_DATANODE_INFO
argument_list|,
name|hostName
argument_list|,
name|uuid
argument_list|,
name|ipAddr
argument_list|,
name|xferPort
argument_list|,
name|infoPort
argument_list|,
name|ipcPort
argument_list|,
name|securePort
argument_list|,
name|containerPort
argument_list|)
decl_stmt|;
name|executeSQL
argument_list|(
name|conn
argument_list|,
name|insertMachineInfo
argument_list|)
expr_stmt|;
name|uuidChecked
operator|.
name|add
argument_list|(
name|uuid
argument_list|)
expr_stmt|;
block|}
name|String
name|insertContainerMembers
init|=
name|String
operator|.
name|format
argument_list|(
name|INSERT_CONTAINER_MEMBERS
argument_list|,
name|containerName
argument_list|,
name|uuid
argument_list|)
decl_stmt|;
name|executeSQL
argument_list|(
name|conn
argument_list|,
name|insertContainerMembers
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Insertion completed."
argument_list|)
expr_stmt|;
block|}
comment|/**    * Converts block.db to sqlite. This is rather simple db, the schema has only    * one table:    *    * blockContainer    * --------------------------    * blockKey*  | containerName    * --------------------------    *    * @param dbPath path to container db.    * @param outPath path to output sqlite    * @throws IOException throws exception.    */
DECL|method|convertBlockDB (Path dbPath, Path outPath)
specifier|private
name|void
name|convertBlockDB
parameter_list|(
name|Path
name|dbPath
parameter_list|,
name|Path
name|outPath
parameter_list|)
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Create tables for sql block db."
argument_list|)
expr_stmt|;
name|File
name|dbFile
init|=
name|dbPath
operator|.
name|toFile
argument_list|()
decl_stmt|;
name|org
operator|.
name|iq80
operator|.
name|leveldb
operator|.
name|Options
name|dbOptions
init|=
operator|new
name|org
operator|.
name|iq80
operator|.
name|leveldb
operator|.
name|Options
argument_list|()
decl_stmt|;
try|try
init|(
name|LevelDBStore
name|dbStore
init|=
operator|new
name|LevelDBStore
argument_list|(
name|dbFile
argument_list|,
name|dbOptions
argument_list|)
init|;
name|Connection
name|conn
operator|=
name|connectDB
argument_list|(
name|outPath
operator|.
name|toString
argument_list|()
argument_list|)
init|;
name|DBIterator
name|iter
operator|=
name|dbStore
operator|.
name|getIterator
argument_list|()
init|)
block|{
name|executeSQL
argument_list|(
name|conn
argument_list|,
name|CREATE_BLOCK_CONTAINER
argument_list|)
expr_stmt|;
name|iter
operator|.
name|seekToFirst
argument_list|()
expr_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|byte
index|[]
argument_list|,
name|byte
index|[]
argument_list|>
name|entry
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|blockKey
init|=
name|DFSUtilClient
operator|.
name|bytes2String
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|containerName
init|=
name|DFSUtilClient
operator|.
name|bytes2String
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|insertBlockContainer
init|=
name|String
operator|.
name|format
argument_list|(
name|INSERT_BLOCK_CONTAINER
argument_list|,
name|blockKey
argument_list|,
name|containerName
argument_list|)
decl_stmt|;
name|executeSQL
argument_list|(
name|conn
argument_list|,
name|insertBlockContainer
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Converts nodePool.db to sqlite. The schema of sql db:    * two tables, nodePool and datanodeInfo (the same datanode Info as for    * container.db).    *    * nodePool    * ---------------------------------------------------------    * datanodeUUID* | poolName*    * ---------------------------------------------------------    *    * datanodeInfo:    * ---------------------------------------------------------    * hostname | datanodeUUid* | xferPort | infoPort | ipcPort    * ---------------------------------------------------------    *    * --------------------------------    * | infoSecurePort | containerPort    * --------------------------------    *    * @param dbPath path to container db.    * @param outPath path to output sqlite    * @throws IOException throws exception.    */
DECL|method|convertNodePoolDB (Path dbPath, Path outPath)
specifier|private
name|void
name|convertNodePoolDB
parameter_list|(
name|Path
name|dbPath
parameter_list|,
name|Path
name|outPath
parameter_list|)
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Create table for sql node pool db."
argument_list|)
expr_stmt|;
name|File
name|dbFile
init|=
name|dbPath
operator|.
name|toFile
argument_list|()
decl_stmt|;
name|org
operator|.
name|iq80
operator|.
name|leveldb
operator|.
name|Options
name|dbOptions
init|=
operator|new
name|org
operator|.
name|iq80
operator|.
name|leveldb
operator|.
name|Options
argument_list|()
decl_stmt|;
try|try
init|(
name|LevelDBStore
name|dbStore
init|=
operator|new
name|LevelDBStore
argument_list|(
name|dbFile
argument_list|,
name|dbOptions
argument_list|)
init|;
name|Connection
name|conn
operator|=
name|connectDB
argument_list|(
name|outPath
operator|.
name|toString
argument_list|()
argument_list|)
init|;
name|DBIterator
name|iter
operator|=
name|dbStore
operator|.
name|getIterator
argument_list|()
init|)
block|{
name|executeSQL
argument_list|(
name|conn
argument_list|,
name|CREATE_NODE_POOL
argument_list|)
expr_stmt|;
name|executeSQL
argument_list|(
name|conn
argument_list|,
name|CREATE_DATANODE_INFO
argument_list|)
expr_stmt|;
name|iter
operator|.
name|seekToFirst
argument_list|()
expr_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|byte
index|[]
argument_list|,
name|byte
index|[]
argument_list|>
name|entry
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|DatanodeID
name|nodeId
init|=
name|DatanodeID
operator|.
name|getFromProtoBuf
argument_list|(
name|HdfsProtos
operator|.
name|DatanodeIDProto
operator|.
name|PARSER
operator|.
name|parseFrom
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|blockPool
init|=
name|DFSUtil
operator|.
name|bytes2String
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
decl_stmt|;
name|insertNodePoolDB
argument_list|(
name|conn
argument_list|,
name|blockPool
argument_list|,
name|nodeId
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|insertNodePoolDB (Connection conn, String blockPool, DatanodeID datanodeID)
specifier|private
name|void
name|insertNodePoolDB
parameter_list|(
name|Connection
name|conn
parameter_list|,
name|String
name|blockPool
parameter_list|,
name|DatanodeID
name|datanodeID
parameter_list|)
throws|throws
name|SQLException
block|{
name|String
name|insertNodePool
init|=
name|String
operator|.
name|format
argument_list|(
name|INSERT_NODE_POOL
argument_list|,
name|datanodeID
operator|.
name|getDatanodeUuid
argument_list|()
argument_list|,
name|blockPool
argument_list|)
decl_stmt|;
name|executeSQL
argument_list|(
name|conn
argument_list|,
name|insertNodePool
argument_list|)
expr_stmt|;
name|String
name|insertDatanodeID
init|=
name|String
operator|.
name|format
argument_list|(
name|INSERT_DATANODE_INFO
argument_list|,
name|datanodeID
operator|.
name|getHostName
argument_list|()
argument_list|,
name|datanodeID
operator|.
name|getDatanodeUuid
argument_list|()
argument_list|,
name|datanodeID
operator|.
name|getIpAddr
argument_list|()
argument_list|,
name|datanodeID
operator|.
name|getXferPort
argument_list|()
argument_list|,
name|datanodeID
operator|.
name|getInfoPort
argument_list|()
argument_list|,
name|datanodeID
operator|.
name|getIpcPort
argument_list|()
argument_list|,
name|datanodeID
operator|.
name|getInfoSecurePort
argument_list|()
argument_list|,
name|datanodeID
operator|.
name|getContainerPort
argument_list|()
argument_list|)
decl_stmt|;
name|executeSQL
argument_list|(
name|conn
argument_list|,
name|insertDatanodeID
argument_list|)
expr_stmt|;
block|}
comment|/**    * Convert openContainer.db to sqlite db file. This is rather simple db,    * the schema has only one table:    *    * openContainer    * -------------------------------    * containerName* | containerUsed    * -------------------------------    *    * @param dbPath path to container db.    * @param outPath path to output sqlite    * @throws IOException throws exception.    */
DECL|method|convertOpenContainerDB (Path dbPath, Path outPath)
specifier|private
name|void
name|convertOpenContainerDB
parameter_list|(
name|Path
name|dbPath
parameter_list|,
name|Path
name|outPath
parameter_list|)
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Create table for open container db."
argument_list|)
expr_stmt|;
name|File
name|dbFile
init|=
name|dbPath
operator|.
name|toFile
argument_list|()
decl_stmt|;
name|org
operator|.
name|iq80
operator|.
name|leveldb
operator|.
name|Options
name|dbOptions
init|=
operator|new
name|org
operator|.
name|iq80
operator|.
name|leveldb
operator|.
name|Options
argument_list|()
decl_stmt|;
try|try
init|(
name|LevelDBStore
name|dbStore
init|=
operator|new
name|LevelDBStore
argument_list|(
name|dbFile
argument_list|,
name|dbOptions
argument_list|)
init|;
name|Connection
name|conn
operator|=
name|connectDB
argument_list|(
name|outPath
operator|.
name|toString
argument_list|()
argument_list|)
init|;
name|DBIterator
name|iter
operator|=
name|dbStore
operator|.
name|getIterator
argument_list|()
init|)
block|{
name|executeSQL
argument_list|(
name|conn
argument_list|,
name|CREATE_OPEN_CONTAINER
argument_list|)
expr_stmt|;
name|iter
operator|.
name|seekToFirst
argument_list|()
expr_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|byte
index|[]
argument_list|,
name|byte
index|[]
argument_list|>
name|entry
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|containerName
init|=
name|DFSUtil
operator|.
name|bytes2String
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
name|Long
name|containerUsed
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|DFSUtil
operator|.
name|bytes2String
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|insertOpenContainer
init|=
name|String
operator|.
name|format
argument_list|(
name|INSERT_OPEN_CONTAINER
argument_list|,
name|containerName
argument_list|,
name|containerUsed
argument_list|)
decl_stmt|;
name|executeSQL
argument_list|(
name|conn
argument_list|,
name|insertOpenContainer
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|parseArgs (String[] argv)
specifier|private
name|CommandLine
name|parseArgs
parameter_list|(
name|String
index|[]
name|argv
parameter_list|)
throws|throws
name|ParseException
block|{
return|return
name|parser
operator|.
name|parse
argument_list|(
name|options
argument_list|,
name|argv
argument_list|)
return|;
block|}
DECL|method|main (String[] args)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|Tool
name|shell
init|=
operator|new
name|SQLCLI
argument_list|()
decl_stmt|;
name|int
name|res
init|=
literal|0
decl_stmt|;
try|try
block|{
name|ToolRunner
operator|.
name|run
argument_list|(
name|shell
argument_list|,
name|args
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|ex
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|res
operator|=
literal|1
expr_stmt|;
block|}
name|System
operator|.
name|exit
argument_list|(
name|res
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

