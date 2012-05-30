begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.contrib.bkjournal
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|contrib
operator|.
name|bkjournal
package|;
end_package

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
name|net
operator|.
name|InetAddress
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
name|logging
operator|.
name|Log
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
name|logging
operator|.
name|LogFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|CreateMode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|KeeperException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|ZooKeeper
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|KeeperException
operator|.
name|NodeExistsException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|ZooDefs
operator|.
name|Ids
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|data
operator|.
name|Stat
import|;
end_import

begin_comment
comment|/**  * Distributed write permission lock, using ZooKeeper. Read the version number  * and return the current inprogress node path available in CurrentInprogress  * path. If it exist, caller can treat that some other client already operating  * on it. Then caller can take action. If there is no inprogress node exist,  * then caller can treat that there is no client operating on it. Later same  * caller should update the his newly created inprogress node path. At this  * point, if some other activities done on this node, version number might  * change, so update will fail. So, this read, update api will ensure that there  * is only node can continue further after checking with CurrentInprogress.  */
end_comment

begin_class
DECL|class|CurrentInprogress
class|class
name|CurrentInprogress
block|{
DECL|field|CONTENT_DELIMITER
specifier|private
specifier|static
specifier|final
name|String
name|CONTENT_DELIMITER
init|=
literal|","
decl_stmt|;
DECL|field|LOG
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|CurrentInprogress
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|zkc
specifier|private
specifier|final
name|ZooKeeper
name|zkc
decl_stmt|;
DECL|field|currentInprogressNode
specifier|private
specifier|final
name|String
name|currentInprogressNode
decl_stmt|;
DECL|field|versionNumberForPermission
specifier|private
specifier|volatile
name|int
name|versionNumberForPermission
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|CURRENT_INPROGRESS_LAYOUT_VERSION
specifier|private
specifier|static
specifier|final
name|int
name|CURRENT_INPROGRESS_LAYOUT_VERSION
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|hostName
specifier|private
specifier|final
name|String
name|hostName
init|=
name|InetAddress
operator|.
name|getLocalHost
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
DECL|method|CurrentInprogress (ZooKeeper zkc, String lockpath)
name|CurrentInprogress
parameter_list|(
name|ZooKeeper
name|zkc
parameter_list|,
name|String
name|lockpath
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|currentInprogressNode
operator|=
name|lockpath
expr_stmt|;
name|this
operator|.
name|zkc
operator|=
name|zkc
expr_stmt|;
try|try
block|{
name|Stat
name|isCurrentInprogressNodeExists
init|=
name|zkc
operator|.
name|exists
argument_list|(
name|lockpath
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|isCurrentInprogressNodeExists
operator|==
literal|null
condition|)
block|{
try|try
block|{
name|zkc
operator|.
name|create
argument_list|(
name|lockpath
argument_list|,
literal|null
argument_list|,
name|Ids
operator|.
name|OPEN_ACL_UNSAFE
argument_list|,
name|CreateMode
operator|.
name|PERSISTENT
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NodeExistsException
name|e
parameter_list|)
block|{
comment|// Node might created by other process at the same time. Ignore it.
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|lockpath
operator|+
literal|" already created by other process."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|KeeperException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Exception accessing Zookeeper"
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Interrupted accessing Zookeeper"
argument_list|,
name|ie
argument_list|)
throw|;
block|}
block|}
comment|/**    * Update the path with prepending version number and hostname    *     * @param path    *          - to be updated in zookeeper    * @throws IOException    */
DECL|method|update (String path)
name|void
name|update
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|content
init|=
name|CURRENT_INPROGRESS_LAYOUT_VERSION
operator|+
name|CONTENT_DELIMITER
operator|+
name|hostName
operator|+
name|CONTENT_DELIMITER
operator|+
name|path
decl_stmt|;
try|try
block|{
name|zkc
operator|.
name|setData
argument_list|(
name|this
operator|.
name|currentInprogressNode
argument_list|,
name|content
operator|.
name|getBytes
argument_list|()
argument_list|,
name|this
operator|.
name|versionNumberForPermission
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|KeeperException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Exception when setting the data "
operator|+
literal|"[layout version number,hostname,inprogressNode path]= ["
operator|+
name|content
operator|+
literal|"] to CurrentInprogress. "
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Interrupted while setting the data "
operator|+
literal|"[layout version number,hostname,inprogressNode path]= ["
operator|+
name|content
operator|+
literal|"] to CurrentInprogress"
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Updated data[layout version number,hostname,inprogressNode path]"
operator|+
literal|"= ["
operator|+
name|content
operator|+
literal|"] to CurrentInprogress"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Read the CurrentInprogress node data from Zookeeper and also get the znode    * version number. Return the 3rd field from the data. i.e saved path with    * #update api    *     * @return available inprogress node path. returns null if not available.    * @throws IOException    */
DECL|method|read ()
name|String
name|read
parameter_list|()
throws|throws
name|IOException
block|{
name|Stat
name|stat
init|=
operator|new
name|Stat
argument_list|()
decl_stmt|;
name|byte
index|[]
name|data
init|=
literal|null
decl_stmt|;
try|try
block|{
name|data
operator|=
name|zkc
operator|.
name|getData
argument_list|(
name|this
operator|.
name|currentInprogressNode
argument_list|,
literal|false
argument_list|,
name|stat
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|KeeperException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Exception while reading the data from "
operator|+
name|currentInprogressNode
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Interrupted while reading data from "
operator|+
name|currentInprogressNode
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|this
operator|.
name|versionNumberForPermission
operator|=
name|stat
operator|.
name|getVersion
argument_list|()
expr_stmt|;
if|if
condition|(
name|data
operator|!=
literal|null
condition|)
block|{
name|String
name|stringData
init|=
operator|new
name|String
argument_list|(
name|data
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Read data[layout version number,hostname,inprogressNode path]"
operator|+
literal|"= ["
operator|+
name|stringData
operator|+
literal|"] from CurrentInprogress"
argument_list|)
expr_stmt|;
name|String
index|[]
name|contents
init|=
name|stringData
operator|.
name|split
argument_list|(
name|CONTENT_DELIMITER
argument_list|)
decl_stmt|;
assert|assert
name|contents
operator|.
name|length
operator|==
literal|3
operator|:
literal|"As per the current data format, "
operator|+
literal|"CurrentInprogress node data should contain 3 fields. "
operator|+
literal|"i.e layout version number,hostname,inprogressNode path"
assert|;
name|String
name|layoutVersion
init|=
name|contents
index|[
literal|0
index|]
decl_stmt|;
if|if
condition|(
name|Long
operator|.
name|valueOf
argument_list|(
name|layoutVersion
argument_list|)
operator|>
name|CURRENT_INPROGRESS_LAYOUT_VERSION
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Supported layout version of CurrentInprogress node is : "
operator|+
name|CURRENT_INPROGRESS_LAYOUT_VERSION
operator|+
literal|" . Layout version of CurrentInprogress node in ZK is : "
operator|+
name|layoutVersion
argument_list|)
throw|;
block|}
name|String
name|inprogressNodePath
init|=
name|contents
index|[
literal|2
index|]
decl_stmt|;
return|return
name|inprogressNodePath
return|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"No data available in CurrentInprogress"
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
comment|/** Clear the CurrentInprogress node data */
DECL|method|clear ()
name|void
name|clear
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|zkc
operator|.
name|setData
argument_list|(
name|this
operator|.
name|currentInprogressNode
argument_list|,
literal|null
argument_list|,
name|versionNumberForPermission
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|KeeperException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Exception when setting the data to CurrentInprogress node"
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Interrupted when setting the data to CurrentInprogress node"
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Cleared the data from CurrentInprogress"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

