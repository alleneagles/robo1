using J2i.Net.XInputWrapper;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace First.Test
{
    public partial class Form1 : Form
    {
        private System.Timers.Timer _pollingTimer;

        public Form1()
        {
            InitializeComponent();

            XboxController.StartPolling();

            _pollingTimer = new System.Timers.Timer(25);
            _pollingTimer.AutoReset = true;
            _pollingTimer.Elapsed += _pollingTimer_Elapsed;
            _pollingTimer.Enabled = true;
        }

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                XboxController.StopPolling();

                components.Dispose();
            }
            base.Dispose(disposing);
        }

        void _pollingTimer_Elapsed(object sender, System.Timers.ElapsedEventArgs e)
        {
            double L, R, C, f, t, s;
            CalcStrafeDrive(out L, out R, out C, out f, out t, out s);

            this.Invoke(new Action(() =>
            {
                txtL.Text = string.Format("{0:F4}", L);
                txtR.Text = string.Format("{0:F4}", R);
                txtC.Text = string.Format("{0:F4}", C);
                txtF.Text = string.Format("{0:F4}", f);
                txtT.Text = string.Format("{0:F4}", t);
                txtS.Text = string.Format("{0:F4}", s);
            }));
        }

        private double SignificantDigits(double value, int digitCount)
        {
            double factor = Math.Pow(10, digitCount);
            int trunc = (int)(factor * value);
            return trunc / factor;
        }

        public void CalcStrafeDrive(out double L, out double R, out double C, out double f, out double t, out double s)
        {
            double Lfix = 1.0;
            double Rfix = 1.0;
            double Sfix = 1.0;

            var controller = XboxController.RetrieveController(0);

            f = controller.LeftThumbStick.Y / (double)short.MaxValue;
            s = controller.LeftThumbStick.X / (double)short.MaxValue;
            t = controller.RightThumbStick.X / (double)short.MaxValue;

            f = SignificantDigits(f, 2);
            s = SignificantDigits(s, 2);
            t = SignificantDigits(t, 2);

            double numerL = Math.Pow(f * 0.5, 2) + Math.Pow(t * 0.5, 2);
            double numerR = Math.Pow(f * 0.5, 2) - Math.Pow(t * 0.5, 2);
            double denom = Math.Pow(0.5, 2) + Math.Pow(0.5, 2);

            L = (numerL / denom) * Lfix;
            R = (numerR / denom) * Rfix;
            C = Math.Pow(s, 2) * Sfix;

            L = SignificantDigits(L, 2);
            R = SignificantDigits(R, 2);
            C = SignificantDigits(C, 2);

            if (t < 0)
            {
                // swap L and R
                double temp = L;
                L = R;
                R = temp;
            }

            // this should be greater than some minimum threshold because a very small negative value,
            // even when the left stick is still, could cause the left and right motors to get messed up
            if (f < -0.1)
            {
                // invert the signs for L and R
                L = -L;
                R = -R;
            }

            if (s < 0)
            {
                // invert the sign for C
                C = -C;
            }
        }

    }
}
