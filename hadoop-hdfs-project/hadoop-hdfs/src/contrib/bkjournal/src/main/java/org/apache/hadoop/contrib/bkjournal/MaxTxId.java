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

begin_comment
comment|/**  * Utility class for storing and reading  * the max seen txid in zookeeper  */
end_comment

begin_class
DECL|class|MaxTxId
class|class
name|MaxTxId
block|{
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
name|MaxTxId
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
DECL|field|path
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
DECL|field|currentStat
specifier|private
name|Stat
name|currentStat
decl_stmt|;
DECL|method|MaxTxId (ZooKeeper zkc, String path)
name|MaxTxId
parameter_list|(
name|ZooKeeper
name|zkc
parameter_list|,
name|String
name|path
parameter_list|)
block|{
name|this
operator|.
name|zkc
operator|=
name|zkc
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
block|}
DECL|method|store (long maxTxId)
specifier|synchronized
name|void
name|store
parameter_list|(
name|long
name|maxTxId
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|currentMax
init|=
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|currentMax
operator|<
name|maxTxId
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"Setting maxTxId to "
operator|+
name|maxTxId
argument_list|)
expr_stmt|;
block|}
name|String
name|txidStr
init|=
name|Long
operator|.
name|toString
argument_list|(
name|maxTxId
argument_list|)
decl_stmt|;
try|try
block|{
if|if
condition|(
name|currentStat
operator|!=
literal|null
condition|)
block|{
name|currentStat
operator|=
name|zkc
operator|.
name|setData
argument_list|(
name|path
argument_list|,
name|txidStr
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|,
name|currentStat
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|zkc
operator|.
name|create
argument_list|(
name|path
argument_list|,
name|txidStr
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
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
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Error writing max tx id"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
DECL|method|get ()
specifier|synchronized
name|long
name|get
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|currentStat
operator|=
name|zkc
operator|.
name|exists
argument_list|(
name|path
argument_list|,
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|currentStat
operator|==
literal|null
condition|)
block|{
return|return
literal|0
return|;
block|}
else|else
block|{
name|byte
index|[]
name|bytes
init|=
name|zkc
operator|.
name|getData
argument_list|(
name|path
argument_list|,
literal|false
argument_list|,
name|currentStat
argument_list|)
decl_stmt|;
name|String
name|txidString
init|=
operator|new
name|String
argument_list|(
name|bytes
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
return|return
name|Long
operator|.
name|valueOf
argument_list|(
name|txidString
argument_list|)
return|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Error reading the max tx id from zk"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

