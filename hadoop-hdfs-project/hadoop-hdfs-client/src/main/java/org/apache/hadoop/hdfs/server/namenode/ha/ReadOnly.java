begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode.ha
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
name|namenode
operator|.
name|ha
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
name|ElementType
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|Inherited
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|Retention
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|RetentionPolicy
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|Target
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

begin_comment
comment|/**  * Marker interface used to annotate methods that are readonly.  */
end_comment

begin_annotation_defn
annotation|@
name|Inherited
annotation|@
name|Retention
argument_list|(
name|RetentionPolicy
operator|.
name|RUNTIME
argument_list|)
annotation|@
name|Target
argument_list|(
name|ElementType
operator|.
name|METHOD
argument_list|)
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|annotation|ReadOnly
specifier|public
annotation_defn|@interface
name|ReadOnly
block|{
comment|/**    * @return if true, the annotated method may update the last accessed time    * while performing its read, if access time is enabled.    */
DECL|method|atimeAffected ()
DECL|field|false
name|boolean
name|atimeAffected
parameter_list|()
default|default
literal|false
function_decl|;
comment|/**    * @return if true, the target method should only be invoked on the active    * namenode. This applies to operations that need to access information that    * is only available on the active namenode.    */
DECL|method|activeOnly ()
DECL|field|false
name|boolean
name|activeOnly
parameter_list|()
default|default
literal|false
function_decl|;
comment|/**    * @return if true, when processing the rpc call of the target method, the    * server side will wait if server state id is behind client (msync). If    * false, the method will be processed regardless of server side state.    */
DECL|method|isCoordinated ()
DECL|field|false
name|boolean
name|isCoordinated
parameter_list|()
default|default
literal|false
function_decl|;
block|}
end_annotation_defn

end_unit

