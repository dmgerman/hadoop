begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred.nativetask
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|nativetask
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
name|mapred
operator|.
name|JobConf
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
name|mapred
operator|.
name|Task
operator|.
name|TaskReporter
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
name|mapred
operator|.
name|TaskAttemptID
import|;
end_import

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|TaskContext
specifier|public
class|class
name|TaskContext
block|{
DECL|field|conf
specifier|private
specifier|final
name|JobConf
name|conf
decl_stmt|;
DECL|field|iKClass
specifier|private
name|Class
argument_list|<
name|?
argument_list|>
name|iKClass
decl_stmt|;
DECL|field|iVClass
specifier|private
name|Class
argument_list|<
name|?
argument_list|>
name|iVClass
decl_stmt|;
DECL|field|oKClass
specifier|private
name|Class
argument_list|<
name|?
argument_list|>
name|oKClass
decl_stmt|;
DECL|field|oVClass
specifier|private
name|Class
argument_list|<
name|?
argument_list|>
name|oVClass
decl_stmt|;
DECL|field|reporter
specifier|private
specifier|final
name|TaskReporter
name|reporter
decl_stmt|;
DECL|field|taskAttemptID
specifier|private
specifier|final
name|TaskAttemptID
name|taskAttemptID
decl_stmt|;
DECL|method|TaskContext (JobConf conf, Class<?> iKClass, Class<?> iVClass, Class<?> oKClass, Class<?> oVClass, TaskReporter reporter, TaskAttemptID id)
specifier|public
name|TaskContext
parameter_list|(
name|JobConf
name|conf
parameter_list|,
name|Class
argument_list|<
name|?
argument_list|>
name|iKClass
parameter_list|,
name|Class
argument_list|<
name|?
argument_list|>
name|iVClass
parameter_list|,
name|Class
argument_list|<
name|?
argument_list|>
name|oKClass
parameter_list|,
name|Class
argument_list|<
name|?
argument_list|>
name|oVClass
parameter_list|,
name|TaskReporter
name|reporter
parameter_list|,
name|TaskAttemptID
name|id
parameter_list|)
block|{
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|this
operator|.
name|iKClass
operator|=
name|iKClass
expr_stmt|;
name|this
operator|.
name|iVClass
operator|=
name|iVClass
expr_stmt|;
name|this
operator|.
name|oKClass
operator|=
name|oKClass
expr_stmt|;
name|this
operator|.
name|oVClass
operator|=
name|oVClass
expr_stmt|;
name|this
operator|.
name|reporter
operator|=
name|reporter
expr_stmt|;
name|this
operator|.
name|taskAttemptID
operator|=
name|id
expr_stmt|;
block|}
DECL|method|getInputKeyClass ()
specifier|public
name|Class
argument_list|<
name|?
argument_list|>
name|getInputKeyClass
parameter_list|()
block|{
return|return
name|iKClass
return|;
block|}
DECL|method|setInputKeyClass (Class<?> klass)
specifier|public
name|void
name|setInputKeyClass
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|klass
parameter_list|)
block|{
name|this
operator|.
name|iKClass
operator|=
name|klass
expr_stmt|;
block|}
DECL|method|getInputValueClass ()
specifier|public
name|Class
argument_list|<
name|?
argument_list|>
name|getInputValueClass
parameter_list|()
block|{
return|return
name|iVClass
return|;
block|}
DECL|method|setInputValueClass (Class<?> klass)
specifier|public
name|void
name|setInputValueClass
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|klass
parameter_list|)
block|{
name|this
operator|.
name|iVClass
operator|=
name|klass
expr_stmt|;
block|}
DECL|method|getOutputKeyClass ()
specifier|public
name|Class
argument_list|<
name|?
argument_list|>
name|getOutputKeyClass
parameter_list|()
block|{
return|return
name|this
operator|.
name|oKClass
return|;
block|}
DECL|method|setOutputKeyClass (Class<?> klass)
specifier|public
name|void
name|setOutputKeyClass
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|klass
parameter_list|)
block|{
name|this
operator|.
name|oKClass
operator|=
name|klass
expr_stmt|;
block|}
DECL|method|getOutputValueClass ()
specifier|public
name|Class
argument_list|<
name|?
argument_list|>
name|getOutputValueClass
parameter_list|()
block|{
return|return
name|this
operator|.
name|oVClass
return|;
block|}
DECL|method|setOutputValueClass (Class<?> klass)
specifier|public
name|void
name|setOutputValueClass
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|klass
parameter_list|)
block|{
name|this
operator|.
name|oVClass
operator|=
name|klass
expr_stmt|;
block|}
DECL|method|getTaskReporter ()
specifier|public
name|TaskReporter
name|getTaskReporter
parameter_list|()
block|{
return|return
name|this
operator|.
name|reporter
return|;
block|}
DECL|method|getTaskAttemptId ()
specifier|public
name|TaskAttemptID
name|getTaskAttemptId
parameter_list|()
block|{
return|return
name|this
operator|.
name|taskAttemptID
return|;
block|}
DECL|method|getConf ()
specifier|public
name|JobConf
name|getConf
parameter_list|()
block|{
return|return
name|this
operator|.
name|conf
return|;
block|}
DECL|method|copyOf ()
specifier|public
name|TaskContext
name|copyOf
parameter_list|()
block|{
return|return
operator|new
name|TaskContext
argument_list|(
name|conf
argument_list|,
name|iKClass
argument_list|,
name|iVClass
argument_list|,
name|oKClass
argument_list|,
name|oVClass
argument_list|,
name|reporter
argument_list|,
name|taskAttemptID
argument_list|)
return|;
block|}
block|}
end_class

end_unit

