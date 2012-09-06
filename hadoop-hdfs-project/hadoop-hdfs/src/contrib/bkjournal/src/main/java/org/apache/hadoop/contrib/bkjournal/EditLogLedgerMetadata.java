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
name|util
operator|.
name|Comparator
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
name|KeeperException
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
name|HdfsConstants
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
name|hadoop
operator|.
name|contrib
operator|.
name|bkjournal
operator|.
name|BKJournalProtos
operator|.
name|EditLogLedgerProto
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|TextFormat
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Charsets
operator|.
name|UTF_8
import|;
end_import

begin_comment
comment|/**  * Utility class for storing the metadata associated   * with a single edit log segment, stored in a single ledger  */
end_comment

begin_class
DECL|class|EditLogLedgerMetadata
specifier|public
class|class
name|EditLogLedgerMetadata
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
name|EditLogLedgerMetadata
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|zkPath
specifier|private
name|String
name|zkPath
decl_stmt|;
DECL|field|dataLayoutVersion
specifier|private
specifier|final
name|int
name|dataLayoutVersion
decl_stmt|;
DECL|field|ledgerId
specifier|private
specifier|final
name|long
name|ledgerId
decl_stmt|;
DECL|field|firstTxId
specifier|private
specifier|final
name|long
name|firstTxId
decl_stmt|;
DECL|field|lastTxId
specifier|private
name|long
name|lastTxId
decl_stmt|;
DECL|field|inprogress
specifier|private
name|boolean
name|inprogress
decl_stmt|;
DECL|field|COMPARATOR
specifier|public
specifier|static
specifier|final
name|Comparator
name|COMPARATOR
init|=
operator|new
name|Comparator
argument_list|<
name|EditLogLedgerMetadata
argument_list|>
argument_list|()
block|{
specifier|public
name|int
name|compare
parameter_list|(
name|EditLogLedgerMetadata
name|o1
parameter_list|,
name|EditLogLedgerMetadata
name|o2
parameter_list|)
block|{
if|if
condition|(
name|o1
operator|.
name|firstTxId
operator|<
name|o2
operator|.
name|firstTxId
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
elseif|else
if|if
condition|(
name|o1
operator|.
name|firstTxId
operator|==
name|o2
operator|.
name|firstTxId
condition|)
block|{
return|return
literal|0
return|;
block|}
else|else
block|{
return|return
literal|1
return|;
block|}
block|}
block|}
decl_stmt|;
DECL|method|EditLogLedgerMetadata (String zkPath, int dataLayoutVersion, long ledgerId, long firstTxId)
name|EditLogLedgerMetadata
parameter_list|(
name|String
name|zkPath
parameter_list|,
name|int
name|dataLayoutVersion
parameter_list|,
name|long
name|ledgerId
parameter_list|,
name|long
name|firstTxId
parameter_list|)
block|{
name|this
operator|.
name|zkPath
operator|=
name|zkPath
expr_stmt|;
name|this
operator|.
name|dataLayoutVersion
operator|=
name|dataLayoutVersion
expr_stmt|;
name|this
operator|.
name|ledgerId
operator|=
name|ledgerId
expr_stmt|;
name|this
operator|.
name|firstTxId
operator|=
name|firstTxId
expr_stmt|;
name|this
operator|.
name|lastTxId
operator|=
name|HdfsConstants
operator|.
name|INVALID_TXID
expr_stmt|;
name|this
operator|.
name|inprogress
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|EditLogLedgerMetadata (String zkPath, int dataLayoutVersion, long ledgerId, long firstTxId, long lastTxId)
name|EditLogLedgerMetadata
parameter_list|(
name|String
name|zkPath
parameter_list|,
name|int
name|dataLayoutVersion
parameter_list|,
name|long
name|ledgerId
parameter_list|,
name|long
name|firstTxId
parameter_list|,
name|long
name|lastTxId
parameter_list|)
block|{
name|this
operator|.
name|zkPath
operator|=
name|zkPath
expr_stmt|;
name|this
operator|.
name|dataLayoutVersion
operator|=
name|dataLayoutVersion
expr_stmt|;
name|this
operator|.
name|ledgerId
operator|=
name|ledgerId
expr_stmt|;
name|this
operator|.
name|firstTxId
operator|=
name|firstTxId
expr_stmt|;
name|this
operator|.
name|lastTxId
operator|=
name|lastTxId
expr_stmt|;
name|this
operator|.
name|inprogress
operator|=
literal|false
expr_stmt|;
block|}
DECL|method|getZkPath ()
name|String
name|getZkPath
parameter_list|()
block|{
return|return
name|zkPath
return|;
block|}
DECL|method|getFirstTxId ()
name|long
name|getFirstTxId
parameter_list|()
block|{
return|return
name|firstTxId
return|;
block|}
DECL|method|getLastTxId ()
name|long
name|getLastTxId
parameter_list|()
block|{
return|return
name|lastTxId
return|;
block|}
DECL|method|getLedgerId ()
name|long
name|getLedgerId
parameter_list|()
block|{
return|return
name|ledgerId
return|;
block|}
DECL|method|isInProgress ()
name|boolean
name|isInProgress
parameter_list|()
block|{
return|return
name|this
operator|.
name|inprogress
return|;
block|}
DECL|method|getDataLayoutVersion ()
name|int
name|getDataLayoutVersion
parameter_list|()
block|{
return|return
name|this
operator|.
name|dataLayoutVersion
return|;
block|}
DECL|method|finalizeLedger (long newLastTxId)
name|void
name|finalizeLedger
parameter_list|(
name|long
name|newLastTxId
parameter_list|)
block|{
assert|assert
name|this
operator|.
name|lastTxId
operator|==
name|HdfsConstants
operator|.
name|INVALID_TXID
assert|;
name|this
operator|.
name|lastTxId
operator|=
name|newLastTxId
expr_stmt|;
name|this
operator|.
name|inprogress
operator|=
literal|false
expr_stmt|;
block|}
DECL|method|read (ZooKeeper zkc, String path)
specifier|static
name|EditLogLedgerMetadata
name|read
parameter_list|(
name|ZooKeeper
name|zkc
parameter_list|,
name|String
name|path
parameter_list|)
throws|throws
name|IOException
throws|,
name|KeeperException
operator|.
name|NoNodeException
block|{
try|try
block|{
name|byte
index|[]
name|data
init|=
name|zkc
operator|.
name|getData
argument_list|(
name|path
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|EditLogLedgerProto
operator|.
name|Builder
name|builder
init|=
name|EditLogLedgerProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
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
literal|"Reading "
operator|+
name|path
operator|+
literal|" data: "
operator|+
operator|new
name|String
argument_list|(
name|data
argument_list|,
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|TextFormat
operator|.
name|merge
argument_list|(
operator|new
name|String
argument_list|(
name|data
argument_list|,
name|UTF_8
argument_list|)
argument_list|,
name|builder
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|builder
operator|.
name|isInitialized
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Invalid/Incomplete data in znode"
argument_list|)
throw|;
block|}
name|EditLogLedgerProto
name|ledger
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
name|int
name|dataLayoutVersion
init|=
name|ledger
operator|.
name|getDataLayoutVersion
argument_list|()
decl_stmt|;
name|long
name|ledgerId
init|=
name|ledger
operator|.
name|getLedgerId
argument_list|()
decl_stmt|;
name|long
name|firstTxId
init|=
name|ledger
operator|.
name|getFirstTxId
argument_list|()
decl_stmt|;
if|if
condition|(
name|ledger
operator|.
name|hasLastTxId
argument_list|()
condition|)
block|{
name|long
name|lastTxId
init|=
name|ledger
operator|.
name|getLastTxId
argument_list|()
decl_stmt|;
return|return
operator|new
name|EditLogLedgerMetadata
argument_list|(
name|path
argument_list|,
name|dataLayoutVersion
argument_list|,
name|ledgerId
argument_list|,
name|firstTxId
argument_list|,
name|lastTxId
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|EditLogLedgerMetadata
argument_list|(
name|path
argument_list|,
name|dataLayoutVersion
argument_list|,
name|ledgerId
argument_list|,
name|firstTxId
argument_list|)
return|;
block|}
block|}
catch|catch
parameter_list|(
name|KeeperException
operator|.
name|NoNodeException
name|nne
parameter_list|)
block|{
throw|throw
name|nne
throw|;
block|}
catch|catch
parameter_list|(
name|KeeperException
name|ke
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Error reading from zookeeper"
argument_list|,
name|ke
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
literal|"Interrupted reading from zookeeper"
argument_list|,
name|ie
argument_list|)
throw|;
block|}
block|}
DECL|method|write (ZooKeeper zkc, String path)
name|void
name|write
parameter_list|(
name|ZooKeeper
name|zkc
parameter_list|,
name|String
name|path
parameter_list|)
throws|throws
name|IOException
throws|,
name|KeeperException
operator|.
name|NodeExistsException
block|{
name|this
operator|.
name|zkPath
operator|=
name|path
expr_stmt|;
name|EditLogLedgerProto
operator|.
name|Builder
name|builder
init|=
name|EditLogLedgerProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setDataLayoutVersion
argument_list|(
name|dataLayoutVersion
argument_list|)
operator|.
name|setLedgerId
argument_list|(
name|ledgerId
argument_list|)
operator|.
name|setFirstTxId
argument_list|(
name|firstTxId
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|inprogress
condition|)
block|{
name|builder
operator|.
name|setLastTxId
argument_list|(
name|lastTxId
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|zkc
operator|.
name|create
argument_list|(
name|path
argument_list|,
name|TextFormat
operator|.
name|printToString
argument_list|(
name|builder
operator|.
name|build
argument_list|()
argument_list|)
operator|.
name|getBytes
argument_list|(
name|UTF_8
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
catch|catch
parameter_list|(
name|KeeperException
operator|.
name|NodeExistsException
name|nee
parameter_list|)
block|{
throw|throw
name|nee
throw|;
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
literal|"Error creating ledger znode"
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
literal|"Interrupted creating ledger znode"
argument_list|,
name|ie
argument_list|)
throw|;
block|}
block|}
DECL|method|verify (ZooKeeper zkc, String path)
name|boolean
name|verify
parameter_list|(
name|ZooKeeper
name|zkc
parameter_list|,
name|String
name|path
parameter_list|)
block|{
try|try
block|{
name|EditLogLedgerMetadata
name|other
init|=
name|read
argument_list|(
name|zkc
argument_list|,
name|path
argument_list|)
decl_stmt|;
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
literal|"Verifying "
operator|+
name|this
operator|.
name|toString
argument_list|()
operator|+
literal|" against "
operator|+
name|other
argument_list|)
expr_stmt|;
block|}
return|return
name|other
operator|.
name|equals
argument_list|(
name|this
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|KeeperException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Couldn't verify data in "
operator|+
name|path
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ie
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Couldn't verify data in "
operator|+
name|path
argument_list|,
name|ie
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
DECL|method|equals (Object o)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|EditLogLedgerMetadata
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|EditLogLedgerMetadata
name|ol
init|=
operator|(
name|EditLogLedgerMetadata
operator|)
name|o
decl_stmt|;
return|return
name|ledgerId
operator|==
name|ol
operator|.
name|ledgerId
operator|&&
name|dataLayoutVersion
operator|==
name|ol
operator|.
name|dataLayoutVersion
operator|&&
name|firstTxId
operator|==
name|ol
operator|.
name|firstTxId
operator|&&
name|lastTxId
operator|==
name|ol
operator|.
name|lastTxId
return|;
block|}
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|hash
init|=
literal|1
decl_stmt|;
name|hash
operator|=
name|hash
operator|*
literal|31
operator|+
operator|(
name|int
operator|)
name|ledgerId
expr_stmt|;
name|hash
operator|=
name|hash
operator|*
literal|31
operator|+
operator|(
name|int
operator|)
name|firstTxId
expr_stmt|;
name|hash
operator|=
name|hash
operator|*
literal|31
operator|+
operator|(
name|int
operator|)
name|lastTxId
expr_stmt|;
name|hash
operator|=
name|hash
operator|*
literal|31
operator|+
operator|(
name|int
operator|)
name|dataLayoutVersion
expr_stmt|;
return|return
name|hash
return|;
block|}
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"[LedgerId:"
operator|+
name|ledgerId
operator|+
literal|", firstTxId:"
operator|+
name|firstTxId
operator|+
literal|", lastTxId:"
operator|+
name|lastTxId
operator|+
literal|", dataLayoutVersion:"
operator|+
name|dataLayoutVersion
operator|+
literal|"]"
return|;
block|}
block|}
end_class

end_unit

