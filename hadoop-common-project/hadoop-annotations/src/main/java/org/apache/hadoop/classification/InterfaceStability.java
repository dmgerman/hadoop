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
operator|.
name|LimitedPrivate
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
operator|.
name|Private
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
operator|.
name|Public
import|;
end_import

begin_comment
comment|/**  * Annotation to inform users of how much to rely on a particular package,  * class or method not changing over time. Currently the stability can be  * {@link Stable}, {@link Evolving} or {@link Unstable}.<br>  *   *<ul><li>All classes that are annotated with {@link Public} or  * {@link LimitedPrivate} must have InterfaceStability annotation.</li>  *<li>Classes that are {@link Private} are to be considered unstable unless  * a different InterfaceStability annotation states otherwise.</li>  *<li>Incompatible changes must not be made to classes marked as stable.</li>  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|InterfaceStability
specifier|public
class|class
name|InterfaceStability
block|{
comment|/**    * Can evolve while retaining compatibility for minor release boundaries.;     * can break compatibility only at major release (ie. at m.0).    */
annotation|@
name|Documented
DECL|annotation|Stable
specifier|public
annotation_defn|@interface
name|Stable
block|{}
empty_stmt|;
comment|/**    * Evolving, but can break compatibility at minor release (i.e. m.x)    */
annotation|@
name|Documented
DECL|annotation|Evolving
specifier|public
annotation_defn|@interface
name|Evolving
block|{}
empty_stmt|;
comment|/**    * No guarantee is provided as to reliability or stability across any    * level of release granularity.    */
annotation|@
name|Documented
DECL|annotation|Unstable
specifier|public
annotation_defn|@interface
name|Unstable
block|{}
empty_stmt|;
block|}
end_class

end_unit

