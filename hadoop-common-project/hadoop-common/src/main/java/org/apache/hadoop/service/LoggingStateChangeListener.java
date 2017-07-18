begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements.  See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership.  The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *       http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.service
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|service
package|;
end_package

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
operator|.
name|Evolving
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
comment|/**  * This is a state change listener that logs events at INFO level  */
end_comment

begin_class
annotation|@
name|Public
annotation|@
name|Evolving
DECL|class|LoggingStateChangeListener
specifier|public
class|class
name|LoggingStateChangeListener
implements|implements
name|ServiceStateChangeListener
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
name|LoggingStateChangeListener
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|log
specifier|private
specifier|final
name|Logger
name|log
decl_stmt|;
comment|/**    * Log events to the given log    * @param log destination for events    */
DECL|method|LoggingStateChangeListener (Logger log)
specifier|public
name|LoggingStateChangeListener
parameter_list|(
name|Logger
name|log
parameter_list|)
block|{
comment|//force an NPE if a null log came in
name|log
operator|.
name|isDebugEnabled
argument_list|()
expr_stmt|;
name|this
operator|.
name|log
operator|=
name|log
expr_stmt|;
block|}
comment|/**    * Log events to the static log for this class    */
DECL|method|LoggingStateChangeListener ()
specifier|public
name|LoggingStateChangeListener
parameter_list|()
block|{
name|this
argument_list|(
name|LOG
argument_list|)
expr_stmt|;
block|}
comment|/**    * Callback for a state change event: log it    * @param service the service that has changed.    */
annotation|@
name|Override
DECL|method|stateChanged (Service service)
specifier|public
name|void
name|stateChanged
parameter_list|(
name|Service
name|service
parameter_list|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Entry to state "
operator|+
name|service
operator|.
name|getServiceState
argument_list|()
operator|+
literal|" for "
operator|+
name|service
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

