begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred.gridmix
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|gridmix
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
name|hadoop
operator|.
name|conf
operator|.
name|Configuration
import|;
end_import

begin_comment
comment|/**  * Factory passing reduce specification as its last record.  */
end_comment

begin_class
DECL|class|IntermediateRecordFactory
class|class
name|IntermediateRecordFactory
extends|extends
name|RecordFactory
block|{
DECL|field|spec
specifier|private
specifier|final
name|GridmixKey
operator|.
name|Spec
name|spec
decl_stmt|;
DECL|field|factory
specifier|private
specifier|final
name|RecordFactory
name|factory
decl_stmt|;
DECL|field|partition
specifier|private
specifier|final
name|int
name|partition
decl_stmt|;
DECL|field|targetRecords
specifier|private
specifier|final
name|long
name|targetRecords
decl_stmt|;
DECL|field|done
specifier|private
name|boolean
name|done
init|=
literal|false
decl_stmt|;
DECL|field|accRecords
specifier|private
name|long
name|accRecords
init|=
literal|0L
decl_stmt|;
comment|/**    * @param targetBytes Expected byte count.    * @param targetRecords Expected record count; will emit spec records after    *                      this boundary is passed.    * @param partition Reduce to which records are emitted.    * @param spec Specification to emit.    * @param conf Unused.    */
DECL|method|IntermediateRecordFactory (long targetBytes, long targetRecords, int partition, GridmixKey.Spec spec, Configuration conf)
specifier|public
name|IntermediateRecordFactory
parameter_list|(
name|long
name|targetBytes
parameter_list|,
name|long
name|targetRecords
parameter_list|,
name|int
name|partition
parameter_list|,
name|GridmixKey
operator|.
name|Spec
name|spec
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|this
argument_list|(
operator|new
name|AvgRecordFactory
argument_list|(
name|targetBytes
argument_list|,
name|targetRecords
argument_list|,
name|conf
argument_list|)
argument_list|,
name|partition
argument_list|,
name|targetRecords
argument_list|,
name|spec
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
comment|/**    * @param factory Factory from which byte/record counts are obtained.    * @param partition Reduce to which records are emitted.    * @param targetRecords Expected record count; will emit spec records after    *                      this boundary is passed.    * @param spec Specification to emit.    * @param conf Unused.    */
DECL|method|IntermediateRecordFactory (RecordFactory factory, int partition, long targetRecords, GridmixKey.Spec spec, Configuration conf)
specifier|public
name|IntermediateRecordFactory
parameter_list|(
name|RecordFactory
name|factory
parameter_list|,
name|int
name|partition
parameter_list|,
name|long
name|targetRecords
parameter_list|,
name|GridmixKey
operator|.
name|Spec
name|spec
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|spec
operator|=
name|spec
expr_stmt|;
name|this
operator|.
name|factory
operator|=
name|factory
expr_stmt|;
name|this
operator|.
name|partition
operator|=
name|partition
expr_stmt|;
name|this
operator|.
name|targetRecords
operator|=
name|targetRecords
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|next (GridmixKey key, GridmixRecord val)
specifier|public
name|boolean
name|next
parameter_list|(
name|GridmixKey
name|key
parameter_list|,
name|GridmixRecord
name|val
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|key
operator|!=
literal|null
assert|;
specifier|final
name|boolean
name|rslt
init|=
name|factory
operator|.
name|next
argument_list|(
name|key
argument_list|,
name|val
argument_list|)
decl_stmt|;
operator|++
name|accRecords
expr_stmt|;
if|if
condition|(
name|rslt
condition|)
block|{
if|if
condition|(
name|accRecords
operator|<
name|targetRecords
condition|)
block|{
name|key
operator|.
name|setType
argument_list|(
name|GridmixKey
operator|.
name|DATA
argument_list|)
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|int
name|orig
init|=
name|key
operator|.
name|getSize
argument_list|()
decl_stmt|;
name|key
operator|.
name|setType
argument_list|(
name|GridmixKey
operator|.
name|REDUCE_SPEC
argument_list|)
expr_stmt|;
name|spec
operator|.
name|rec_in
operator|=
name|accRecords
expr_stmt|;
name|key
operator|.
name|setSpec
argument_list|(
name|spec
argument_list|)
expr_stmt|;
name|val
operator|.
name|setSize
argument_list|(
name|val
operator|.
name|getSize
argument_list|()
operator|-
operator|(
name|key
operator|.
name|getSize
argument_list|()
operator|-
name|orig
operator|)
argument_list|)
expr_stmt|;
comment|// reset counters
name|accRecords
operator|=
literal|0L
expr_stmt|;
name|spec
operator|.
name|bytes_out
operator|=
literal|0L
expr_stmt|;
name|spec
operator|.
name|rec_out
operator|=
literal|0L
expr_stmt|;
name|done
operator|=
literal|true
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
operator|!
name|done
condition|)
block|{
comment|// ensure spec emitted
name|key
operator|.
name|setType
argument_list|(
name|GridmixKey
operator|.
name|REDUCE_SPEC
argument_list|)
expr_stmt|;
name|key
operator|.
name|setPartition
argument_list|(
name|partition
argument_list|)
expr_stmt|;
name|key
operator|.
name|setSize
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|val
operator|.
name|setSize
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|spec
operator|.
name|rec_in
operator|=
literal|0L
expr_stmt|;
name|key
operator|.
name|setSpec
argument_list|(
name|spec
argument_list|)
expr_stmt|;
name|done
operator|=
literal|true
expr_stmt|;
return|return
literal|true
return|;
block|}
name|key
operator|.
name|setPartition
argument_list|(
name|partition
argument_list|)
expr_stmt|;
return|return
name|rslt
return|;
block|}
annotation|@
name|Override
DECL|method|getProgress ()
specifier|public
name|float
name|getProgress
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|factory
operator|.
name|getProgress
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|factory
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

