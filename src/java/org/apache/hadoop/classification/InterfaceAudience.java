begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.classification
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|classification
package|;
end_package

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|Documented
import|;
end_import

begin_comment
comment|/**  * Annotation to inform users of a package, class or method's intended audience.  */
end_comment

begin_class
DECL|class|InterfaceAudience
specifier|public
class|class
name|InterfaceAudience
block|{
comment|/**    * Intended for use by any project or application.    */
DECL|annotation|Public
annotation|@
name|Documented
specifier|public
annotation_defn|@interface
name|Public
block|{}
empty_stmt|;
comment|/**    * Intended only for the project(s) specified in the annotation    */
DECL|annotation|LimitedPrivate
annotation|@
name|Documented
specifier|public
annotation_defn|@interface
name|LimitedPrivate
block|{
DECL|enum|Project
DECL|enumConstant|COMMON
DECL|enumConstant|AVRO
DECL|enumConstant|CHUKWA
DECL|enumConstant|HBASE
DECL|enumConstant|HDFS
specifier|public
enum|enum
name|Project
block|{
name|COMMON
block|,
name|AVRO
block|,
name|CHUKWA
block|,
name|HBASE
block|,
name|HDFS
block|,
DECL|enumConstant|HIVE
DECL|enumConstant|MAPREDUCE
DECL|enumConstant|PIG
DECL|enumConstant|ZOOKEEPER
name|HIVE
block|,
name|MAPREDUCE
block|,
name|PIG
block|,
name|ZOOKEEPER
block|}
empty_stmt|;
DECL|method|value ()
name|Project
index|[]
name|value
parameter_list|()
function_decl|;
block|}
empty_stmt|;
comment|/**    * Intended for use only within Hadoop itself.    */
DECL|annotation|Private
annotation|@
name|Documented
specifier|public
annotation_defn|@interface
name|Private
block|{}
empty_stmt|;
DECL|method|InterfaceAudience ()
specifier|private
name|InterfaceAudience
parameter_list|()
block|{}
comment|// Audience can't exist on its own
block|}
end_class

end_unit

