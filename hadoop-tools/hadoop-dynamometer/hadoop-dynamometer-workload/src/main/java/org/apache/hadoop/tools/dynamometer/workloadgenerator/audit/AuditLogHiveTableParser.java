begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.tools.dynamometer.workloadgenerator.audit
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
name|workloadgenerator
operator|.
name|audit
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
name|function
operator|.
name|Function
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
name|Text
import|;
end_import

begin_comment
comment|/**  * This {@link AuditCommandParser} is used to read commands assuming that the  * input was generated by a Hive query storing uncompressed output files, in  * which fields should be separated by the start-of-heading (U+0001) character.  * The fields available should be, in order:  *  *<pre>  *   relativeTimestampMs,ugi,command,src,dest,sourceIP  *</pre>  *  * Where relativeTimestampMs represents the time elapsed between the start of  * the audit log and the occurrence of the audit event. Assuming your audit logs  * are available in Hive, this can be generated with a query looking like:  *  *<pre>  *   INSERT OVERWRITE DIRECTORY '${outputPath}'  *   SELECT (timestamp - ${startTime} AS relTime, ugi, cmd, src, dst, ip  *   FROM '${auditLogTableLocation}'  *   WHERE  *     timestamp {@literal>=} ${startTime}  *     AND timestamp {@literal<} ${endTime}  *   DISTRIBUTE BY src  *   SORT BY relTime ASC;  *</pre>  *  * Note that the sorting step is important; events in each distinct file must be  * in time-ascending order.  */
end_comment

begin_class
DECL|class|AuditLogHiveTableParser
specifier|public
class|class
name|AuditLogHiveTableParser
implements|implements
name|AuditCommandParser
block|{
DECL|field|FIELD_SEPARATOR
specifier|private
specifier|static
specifier|final
name|String
name|FIELD_SEPARATOR
init|=
literal|"\u0001"
decl_stmt|;
annotation|@
name|Override
DECL|method|initialize (Configuration conf)
specifier|public
name|void
name|initialize
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Nothing to be done
block|}
annotation|@
name|Override
DECL|method|parse (Text inputLine, Function<Long, Long> relativeToAbsolute)
specifier|public
name|AuditReplayCommand
name|parse
parameter_list|(
name|Text
name|inputLine
parameter_list|,
name|Function
argument_list|<
name|Long
argument_list|,
name|Long
argument_list|>
name|relativeToAbsolute
parameter_list|)
throws|throws
name|IOException
block|{
name|String
index|[]
name|fields
init|=
name|inputLine
operator|.
name|toString
argument_list|()
operator|.
name|split
argument_list|(
name|FIELD_SEPARATOR
argument_list|)
decl_stmt|;
name|long
name|absoluteTimestamp
init|=
name|relativeToAbsolute
operator|.
name|apply
argument_list|(
name|Long
operator|.
name|parseLong
argument_list|(
name|fields
index|[
literal|0
index|]
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|AuditReplayCommand
argument_list|(
name|absoluteTimestamp
argument_list|,
name|fields
index|[
literal|1
index|]
argument_list|,
name|fields
index|[
literal|2
index|]
argument_list|,
name|fields
index|[
literal|3
index|]
argument_list|,
name|fields
index|[
literal|4
index|]
argument_list|,
name|fields
index|[
literal|5
index|]
argument_list|)
return|;
block|}
block|}
end_class

end_unit

