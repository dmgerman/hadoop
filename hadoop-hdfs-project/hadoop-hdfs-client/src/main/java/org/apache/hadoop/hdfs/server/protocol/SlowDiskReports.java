begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.protocol
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|protocol
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableMap
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
name|classification
operator|.
name|InterfaceAudience
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
name|classification
operator|.
name|InterfaceStability
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nullable
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

begin_comment
comment|/**  * A class that allows a DataNode to communicate information about all  * its disks that appear to be slow.  *  * The wire representation of this structure is a list of  * SlowDiskReportProto messages.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|SlowDiskReports
specifier|public
specifier|final
class|class
name|SlowDiskReports
block|{
comment|/**    * A map from the DataNode Disk's BasePath to its mean metadata op latency,    * mean read io latency and mean write io latency.    *    * The NameNode must not attempt to interpret the mean latencies    * beyond exposing them as a diagnostic. e.g. metrics. Also, comparing    * latencies across reports from different DataNodes may not be not    * meaningful and must be avoided.    */
annotation|@
name|Nonnull
DECL|field|slowDisks
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|DiskOp
argument_list|,
name|Double
argument_list|>
argument_list|>
name|slowDisks
decl_stmt|;
comment|/**    * An object representing a SlowDiskReports with no entries. Should    * be used instead of null or creating new objects when there are    * no slow peers to report.    */
DECL|field|EMPTY_REPORT
specifier|public
specifier|static
specifier|final
name|SlowDiskReports
name|EMPTY_REPORT
init|=
operator|new
name|SlowDiskReports
argument_list|(
name|ImmutableMap
operator|.
name|of
argument_list|()
argument_list|)
decl_stmt|;
DECL|method|SlowDiskReports (Map<String, Map<DiskOp, Double>> slowDisks)
specifier|private
name|SlowDiskReports
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|DiskOp
argument_list|,
name|Double
argument_list|>
argument_list|>
name|slowDisks
parameter_list|)
block|{
name|this
operator|.
name|slowDisks
operator|=
name|slowDisks
expr_stmt|;
block|}
DECL|method|create ( @ullable Map<String, Map<DiskOp, Double>> slowDisks)
specifier|public
specifier|static
name|SlowDiskReports
name|create
parameter_list|(
annotation|@
name|Nullable
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|DiskOp
argument_list|,
name|Double
argument_list|>
argument_list|>
name|slowDisks
parameter_list|)
block|{
if|if
condition|(
name|slowDisks
operator|==
literal|null
operator|||
name|slowDisks
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|EMPTY_REPORT
return|;
block|}
return|return
operator|new
name|SlowDiskReports
argument_list|(
name|slowDisks
argument_list|)
return|;
block|}
DECL|method|getSlowDisks ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|DiskOp
argument_list|,
name|Double
argument_list|>
argument_list|>
name|getSlowDisks
parameter_list|()
block|{
return|return
name|slowDisks
return|;
block|}
DECL|method|haveSlowDisks ()
specifier|public
name|boolean
name|haveSlowDisks
parameter_list|()
block|{
return|return
name|slowDisks
operator|.
name|size
argument_list|()
operator|>
literal|0
return|;
block|}
comment|/**    * Return true if the two objects represent the same set slow disk    * entries. Primarily for unit testing convenience.    */
annotation|@
name|Override
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
name|this
operator|==
name|o
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|SlowDiskReports
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|SlowDiskReports
name|that
init|=
operator|(
name|SlowDiskReports
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|slowDisks
operator|.
name|size
argument_list|()
operator|!=
name|that
operator|.
name|slowDisks
operator|.
name|size
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|this
operator|.
name|slowDisks
operator|.
name|keySet
argument_list|()
operator|.
name|containsAll
argument_list|(
name|that
operator|.
name|slowDisks
operator|.
name|keySet
argument_list|()
argument_list|)
operator|||
operator|!
name|that
operator|.
name|slowDisks
operator|.
name|keySet
argument_list|()
operator|.
name|containsAll
argument_list|(
name|this
operator|.
name|slowDisks
operator|.
name|keySet
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|boolean
name|areEqual
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|DiskOp
argument_list|,
name|Double
argument_list|>
argument_list|>
name|entry
range|:
name|this
operator|.
name|slowDisks
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|equals
argument_list|(
name|that
operator|.
name|slowDisks
operator|.
name|get
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|slowDisks
operator|.
name|hashCode
argument_list|()
return|;
block|}
comment|/**    * Lists the types of operations on which disk latencies are measured.    */
DECL|enum|DiskOp
specifier|public
enum|enum
name|DiskOp
block|{
DECL|enumConstant|METADATA
name|METADATA
argument_list|(
literal|"MetadataOp"
argument_list|)
block|,
DECL|enumConstant|READ
name|READ
argument_list|(
literal|"ReadIO"
argument_list|)
block|,
DECL|enumConstant|WRITE
name|WRITE
argument_list|(
literal|"WriteIO"
argument_list|)
block|;
DECL|field|value
specifier|private
specifier|final
name|String
name|value
decl_stmt|;
DECL|method|DiskOp (final String v)
name|DiskOp
parameter_list|(
specifier|final
name|String
name|v
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|v
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|value
return|;
block|}
DECL|method|fromValue (final String value)
specifier|public
specifier|static
name|DiskOp
name|fromValue
parameter_list|(
specifier|final
name|String
name|value
parameter_list|)
block|{
for|for
control|(
name|DiskOp
name|as
range|:
name|DiskOp
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|as
operator|.
name|value
operator|.
name|equals
argument_list|(
name|value
argument_list|)
condition|)
block|{
return|return
name|as
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

