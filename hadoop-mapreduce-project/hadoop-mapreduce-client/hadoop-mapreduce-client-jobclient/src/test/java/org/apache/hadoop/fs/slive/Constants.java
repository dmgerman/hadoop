begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.slive
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|slive
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_comment
comment|/**  * Constants used in various places in slive  */
end_comment

begin_class
DECL|class|Constants
class|class
name|Constants
block|{
comment|/**    * This class should be static members only - no construction allowed    */
DECL|method|Constants ()
specifier|private
name|Constants
parameter_list|()
block|{   }
comment|/**    * The distributions supported (or that maybe supported)    */
DECL|enum|Distribution
enum|enum
name|Distribution
block|{
DECL|enumConstant|BEG
DECL|enumConstant|END
DECL|enumConstant|UNIFORM
DECL|enumConstant|MID
name|BEG
block|,
name|END
block|,
name|UNIFORM
block|,
name|MID
block|;
DECL|method|lowerName ()
name|String
name|lowerName
parameter_list|()
block|{
return|return
name|this
operator|.
name|name
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
return|;
block|}
block|}
comment|/**    * Allowed operation types    */
DECL|enum|OperationType
enum|enum
name|OperationType
block|{
DECL|enumConstant|READ
DECL|enumConstant|APPEND
DECL|enumConstant|RENAME
DECL|enumConstant|LS
DECL|enumConstant|MKDIR
DECL|enumConstant|DELETE
DECL|enumConstant|CREATE
name|READ
block|,
name|APPEND
block|,
name|RENAME
block|,
name|LS
block|,
name|MKDIR
block|,
name|DELETE
block|,
name|CREATE
block|;
DECL|method|lowerName ()
name|String
name|lowerName
parameter_list|()
block|{
return|return
name|this
operator|.
name|name
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
return|;
block|}
block|}
comment|// program info
DECL|field|PROG_NAME
specifier|static
specifier|final
name|String
name|PROG_NAME
init|=
name|SliveTest
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
decl_stmt|;
DECL|field|PROG_VERSION
specifier|static
specifier|final
name|String
name|PROG_VERSION
init|=
literal|"0.0.2"
decl_stmt|;
comment|// useful constants
DECL|field|MEGABYTES
specifier|static
specifier|final
name|int
name|MEGABYTES
init|=
literal|1048576
decl_stmt|;
comment|// must be a multiple of
comment|// BYTES_PER_LONG - used for reading and writing buffer sizes
DECL|field|BUFFERSIZE
specifier|static
specifier|final
name|int
name|BUFFERSIZE
init|=
literal|64
operator|*
literal|1024
decl_stmt|;
comment|// 8 bytes per long
DECL|field|BYTES_PER_LONG
specifier|static
specifier|final
name|int
name|BYTES_PER_LONG
init|=
literal|8
decl_stmt|;
comment|// used for finding the reducer file for a given number
DECL|field|REDUCER_FILE
specifier|static
specifier|final
name|String
name|REDUCER_FILE
init|=
literal|"part-%s"
decl_stmt|;
comment|// this is used to ensure the blocksize is a multiple of this config setting
DECL|field|BYTES_PER_CHECKSUM
specifier|static
specifier|final
name|String
name|BYTES_PER_CHECKSUM
init|=
literal|"io.bytes.per.checksum"
decl_stmt|;
comment|// min replication setting for verification
DECL|field|MIN_REPLICATION
specifier|static
specifier|final
name|String
name|MIN_REPLICATION
init|=
literal|"dfs.namenode.replication.min"
decl_stmt|;
comment|// used for getting an option description given a set of distributions
comment|// to substitute
DECL|field|OP_DESCR
specifier|static
specifier|final
name|String
name|OP_DESCR
init|=
literal|"pct,distribution where distribution is one of %s"
decl_stmt|;
comment|// keys for looking up a specific operation in the hadoop config
DECL|field|OP_PERCENT
specifier|static
specifier|final
name|String
name|OP_PERCENT
init|=
literal|"slive.op.%s.pct"
decl_stmt|;
DECL|field|OP
specifier|static
specifier|final
name|String
name|OP
init|=
literal|"slive.op.%s"
decl_stmt|;
DECL|field|OP_DISTR
specifier|static
specifier|final
name|String
name|OP_DISTR
init|=
literal|"slive.op.%s.dist"
decl_stmt|;
comment|// path constants
DECL|field|BASE_DIR
specifier|static
specifier|final
name|String
name|BASE_DIR
init|=
literal|"slive"
decl_stmt|;
DECL|field|DATA_DIR
specifier|static
specifier|final
name|String
name|DATA_DIR
init|=
literal|"data"
decl_stmt|;
DECL|field|OUTPUT_DIR
specifier|static
specifier|final
name|String
name|OUTPUT_DIR
init|=
literal|"output"
decl_stmt|;
comment|// whether whenever data is written a flush should occur
DECL|field|FLUSH_WRITES
specifier|static
specifier|final
name|boolean
name|FLUSH_WRITES
init|=
literal|false
decl_stmt|;
block|}
end_class

end_unit

