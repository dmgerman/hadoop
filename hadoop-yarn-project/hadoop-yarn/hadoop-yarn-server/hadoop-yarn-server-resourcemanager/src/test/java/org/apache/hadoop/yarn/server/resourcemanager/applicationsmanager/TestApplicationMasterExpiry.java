begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.applicationsmanager
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|applicationsmanager
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ApplicationId
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|YarnApplicationState
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ApplicationSubmissionContext
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
name|yarn
operator|.
name|conf
operator|.
name|YarnConfiguration
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
name|yarn
operator|.
name|event
operator|.
name|EventHandler
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
name|yarn
operator|.
name|factories
operator|.
name|RecordFactory
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
name|yarn
operator|.
name|factory
operator|.
name|providers
operator|.
name|RecordFactoryProvider
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
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|RMContext
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
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|RMContextImpl
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
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|ResourceManager
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
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|amlauncher
operator|.
name|AMLauncherEventType
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
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|rmapp
operator|.
name|attempt
operator|.
name|AMLivelinessMonitor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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

begin_comment
comment|/**  * A test case that tests the expiry of the application master.  * More tests can be added to this.  */
end_comment

begin_class
DECL|class|TestApplicationMasterExpiry
specifier|public
class|class
name|TestApplicationMasterExpiry
block|{
comment|//  private static final Log LOG = LogFactory.getLog(TestApplicationMasterExpiry.class);
comment|//  private static RecordFactory recordFactory = RecordFactoryProvider.getRecordFactory(null);
comment|//
comment|//  private final RMContext context = new RMContextImpl(new MemStore());
comment|//  private AMLivelinessMonitor amLivelinessMonitor;
comment|//
comment|//  @Before
comment|//  public void setUp() {
comment|//    new DummyApplicationTracker();
comment|//    new DummySN();
comment|//    new DummyLauncher();
comment|//    new ApplicationEventTypeListener();
comment|//    Configuration conf = new Configuration();
comment|//    context.getDispatcher().register(ApplicationEventType.class,
comment|//        new ResourceManager.ApplicationEventDispatcher(context));
comment|//    context.getDispatcher().init(conf);
comment|//    context.getDispatcher().start();
comment|//    conf.setLong(YarnConfiguration.AM_EXPIRY_INTERVAL, 1000L);
comment|//    amLivelinessMonitor = new AMLivelinessMonitor(this.context
comment|//        .getDispatcher().getEventHandler());
comment|//    amLivelinessMonitor.init(conf);
comment|//    amLivelinessMonitor.start();
comment|//  }
comment|//
comment|//  private class DummyApplicationTracker implements EventHandler<ASMEvent<ApplicationTrackerEventType>> {
comment|//    DummyApplicationTracker() {
comment|//      context.getDispatcher().register(ApplicationTrackerEventType.class, this);
comment|//    }
comment|//    @Override
comment|//    public void handle(ASMEvent<ApplicationTrackerEventType> event) {
comment|//    }
comment|//  }
comment|//
comment|//  private AtomicInteger expiry = new AtomicInteger();
comment|//  private boolean expired = false;
comment|//
comment|//  private class ApplicationEventTypeListener implements
comment|//      EventHandler<ApplicationEvent> {
comment|//    ApplicationEventTypeListener() {
comment|//      context.getDispatcher().register(ApplicationEventType.class, this);
comment|//    }
comment|//    @Override
comment|//    public void handle(ApplicationEvent event) {
comment|//      switch(event.getType()) {
comment|//      case EXPIRE:
comment|//        expired = true;
comment|//        LOG.info("Received expiry from application " + event.getApplicationId());
comment|//        synchronized(expiry) {
comment|//          expiry.addAndGet(1);
comment|//        }
comment|//      }
comment|//    }
comment|//  }
comment|//
comment|//  private class DummySN implements EventHandler<ASMEvent<SNEventType>> {
comment|//    DummySN() {
comment|//      context.getDispatcher().register(SNEventType.class, this);
comment|//    }
comment|//    @Override
comment|//    public void handle(ASMEvent<SNEventType> event) {
comment|//    }
comment|//  }
comment|//
comment|//  private class DummyLauncher implements EventHandler<ASMEvent<AMLauncherEventType>> {
comment|//    DummyLauncher() {
comment|//      context.getDispatcher().register(AMLauncherEventType.class, this);
comment|//    }
comment|//    @Override
comment|//    public void handle(ASMEvent<AMLauncherEventType> event) {
comment|//    }
comment|//  }
comment|//
comment|//  private void waitForState(AppAttempt application, ApplicationState
comment|//      finalState) throws Exception {
comment|//    int count = 0;
comment|//    while(application.getState() != finalState&& count< 10) {
comment|//      Thread.sleep(500);
comment|//      count++;
comment|//    }
comment|//    Assert.assertEquals(finalState, application.getState());
comment|//  }
comment|//
comment|//  @Test
comment|//  public void testAMExpiry() throws Exception {
comment|//    ApplicationSubmissionContext submissionContext = recordFactory
comment|//        .newRecordInstance(ApplicationSubmissionContext.class);
comment|//    submissionContext.setApplicationId(recordFactory
comment|//        .newRecordInstance(ApplicationId.class));
comment|//    submissionContext.getApplicationId().setClusterTimestamp(
comment|//        System.currentTimeMillis());
comment|//    submissionContext.getApplicationId().setId(1);
comment|//
comment|//    ApplicationStore appStore = context.getApplicationsStore()
comment|//    .createApplicationStore(submissionContext.getApplicationId(),
comment|//        submissionContext);
comment|//    AppAttempt application = new AppAttemptImpl(context,
comment|//        new Configuration(), "dummy", submissionContext, "dummytoken", appStore,
comment|//        amLivelinessMonitor);
comment|//    context.getApplications()
comment|//        .put(application.getApplicationID(), application);
comment|//
comment|//    this.context.getDispatcher().getSyncHandler().handle(
comment|//        new ApplicationEvent(ApplicationEventType.ALLOCATE, submissionContext
comment|//            .getApplicationId()));
comment|//
comment|//    waitForState(application, ApplicationState.ALLOCATING);
comment|//
comment|//    this.context.getDispatcher().getEventHandler().handle(
comment|//        new AMAllocatedEvent(application.getApplicationID(),
comment|//            application.getMasterContainer()));
comment|//
comment|//    waitForState(application, ApplicationState.LAUNCHING);
comment|//
comment|//    this.context.getDispatcher().getEventHandler().handle(
comment|//        new ApplicationEvent(ApplicationEventType.LAUNCHED,
comment|//            application.getApplicationID()));
comment|//    synchronized(expiry) {
comment|//      while (expiry.get() == 0) {
comment|//        expiry.wait(1000);
comment|//      }
comment|//    }
comment|//    Assert.assertTrue(expired);
comment|//  }
block|}
end_class

end_unit

