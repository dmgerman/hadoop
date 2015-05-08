begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.lib.output
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|lib
operator|.
name|output
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
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
name|io
operator|.
name|Writable
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
name|WritableComparable
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
name|MapFile
operator|.
name|Reader
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
name|Partitioner
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|Mockito
import|;
end_import

begin_class
DECL|class|TestMapFileOutputFormat
specifier|public
class|class
name|TestMapFileOutputFormat
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"static-access"
argument_list|)
annotation|@
name|Test
DECL|method|testPartitionerShouldNotBeCalledWhenOneReducerIsPresent ()
specifier|public
name|void
name|testPartitionerShouldNotBeCalledWhenOneReducerIsPresent
parameter_list|()
throws|throws
name|Exception
block|{
name|MapFileOutputFormat
name|outputFormat
init|=
operator|new
name|MapFileOutputFormat
argument_list|()
decl_stmt|;
name|Reader
name|reader
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|Reader
operator|.
name|class
argument_list|)
decl_stmt|;
name|Reader
index|[]
name|readers
init|=
operator|new
name|Reader
index|[]
block|{
name|reader
block|}
decl_stmt|;
name|outputFormat
operator|.
name|getEntry
argument_list|(
name|readers
argument_list|,
operator|new
name|MyPartitioner
argument_list|()
argument_list|,
operator|new
name|Text
argument_list|()
argument_list|,
operator|new
name|Text
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|!
name|MyPartitioner
operator|.
name|isGetPartitionCalled
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|MyPartitioner
operator|.
name|setGetPartitionCalled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|class|MyPartitioner
specifier|private
specifier|static
class|class
name|MyPartitioner
extends|extends
name|Partitioner
argument_list|<
name|WritableComparable
argument_list|,
name|Writable
argument_list|>
block|{
DECL|field|getPartitionCalled
specifier|private
specifier|static
name|boolean
name|getPartitionCalled
init|=
literal|false
decl_stmt|;
DECL|method|isGetPartitionCalled ()
specifier|public
specifier|static
name|boolean
name|isGetPartitionCalled
parameter_list|()
block|{
return|return
name|getPartitionCalled
return|;
block|}
annotation|@
name|Override
DECL|method|getPartition (WritableComparable key, Writable value, int numPartitions)
specifier|public
name|int
name|getPartition
parameter_list|(
name|WritableComparable
name|key
parameter_list|,
name|Writable
name|value
parameter_list|,
name|int
name|numPartitions
parameter_list|)
block|{
name|setGetPartitionCalled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
operator|-
literal|1
return|;
block|}
DECL|method|setGetPartitionCalled (boolean getPartitionCalled)
specifier|public
specifier|static
name|void
name|setGetPartitionCalled
parameter_list|(
name|boolean
name|getPartitionCalled
parameter_list|)
block|{
name|MyPartitioner
operator|.
name|getPartitionCalled
operator|=
name|getPartitionCalled
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

