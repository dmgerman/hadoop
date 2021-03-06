begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.tools.dynamometer.blockgenerator
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|tools
operator|.
name|dynamometer
operator|.
name|blockgenerator
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
name|List
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
name|Configuration
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
name|io
operator|.
name|IntWritable
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
name|io
operator|.
name|LongWritable
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
name|io
operator|.
name|Text
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
name|mapreduce
operator|.
name|Mapper
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

begin_comment
comment|/**  * This Mapper class generates a list of {@link BlockInfo}'s from a given  * fsimage.  *  * Input: fsimage in XML format. It should be generated using  * {@code org.apache.hadoop.hdfs.tools.offlineImageViewer.OfflineImageViewer}.  *  * Output: list of all {@link BlockInfo}'s  */
end_comment

begin_class
DECL|class|XMLParserMapper
specifier|public
class|class
name|XMLParserMapper
extends|extends
name|Mapper
argument_list|<
name|LongWritable
argument_list|,
name|Text
argument_list|,
name|IntWritable
argument_list|,
name|BlockInfo
argument_list|>
block|{
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
name|XMLParserMapper
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Override
DECL|method|setup (Mapper.Context context)
specifier|public
name|void
name|setup
parameter_list|(
name|Mapper
operator|.
name|Context
name|context
parameter_list|)
block|{
name|Configuration
name|conf
init|=
name|context
operator|.
name|getConfiguration
argument_list|()
decl_stmt|;
name|numDataNodes
operator|=
name|conf
operator|.
name|getInt
argument_list|(
name|GenerateBlockImagesDriver
operator|.
name|NUM_DATANODES_KEY
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|parser
operator|=
operator|new
name|XMLParser
argument_list|()
expr_stmt|;
block|}
comment|// Blockindexes should be generated serially
DECL|field|blockIndex
specifier|private
name|int
name|blockIndex
init|=
literal|0
decl_stmt|;
DECL|field|numDataNodes
specifier|private
name|int
name|numDataNodes
decl_stmt|;
DECL|field|parser
specifier|private
name|XMLParser
name|parser
decl_stmt|;
comment|/**    * Read the input XML file line by line, and generate list of blocks. The    * actual parsing logic is handled by {@link XMLParser}. This mapper just    * delegates to that class and then writes the blocks to the corresponding    * index to be processed by reducers.    */
annotation|@
name|Override
DECL|method|map (LongWritable lineNum, Text line, Mapper<LongWritable, Text, IntWritable, BlockInfo>.Context context)
specifier|public
name|void
name|map
parameter_list|(
name|LongWritable
name|lineNum
parameter_list|,
name|Text
name|line
parameter_list|,
name|Mapper
argument_list|<
name|LongWritable
argument_list|,
name|Text
argument_list|,
name|IntWritable
argument_list|,
name|BlockInfo
argument_list|>
operator|.
name|Context
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|List
argument_list|<
name|BlockInfo
argument_list|>
name|blockInfos
init|=
name|parser
operator|.
name|parseLine
argument_list|(
name|line
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|BlockInfo
name|blockInfo
range|:
name|blockInfos
control|)
block|{
for|for
control|(
name|short
name|i
init|=
literal|0
init|;
name|i
operator|<
name|blockInfo
operator|.
name|getReplication
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|context
operator|.
name|write
argument_list|(
operator|new
name|IntWritable
argument_list|(
operator|(
name|blockIndex
operator|+
name|i
operator|)
operator|%
name|numDataNodes
argument_list|)
argument_list|,
name|blockInfo
argument_list|)
expr_stmt|;
block|}
name|blockIndex
operator|++
expr_stmt|;
if|if
condition|(
name|blockIndex
operator|%
literal|1000000
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Processed "
operator|+
name|blockIndex
operator|+
literal|" blocks"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

